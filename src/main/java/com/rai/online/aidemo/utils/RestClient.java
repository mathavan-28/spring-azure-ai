//package com.rai.online.aidemo.utils;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectReader;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode;
//import com.rai.online.aidemo.exceptions.SpringAIDemoException;
//import io.netty.channel.ChannelOption;
//import io.netty.handler.ssl.SslContextBuilder;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.client.reactive.ClientHttpConnector;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.netty.Connection;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.resources.ConnectionProvider;
//import reactor.netty.tcp.SslProvider;
//
//import javax.annotation.PostConstruct;
//import javax.net.ssl.KeyManagerFactory;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.cert.X509Certificate;
//import java.time.Duration;
//import java.time.ZoneOffset;
//import java.time.format.DateTimeFormatter;
//import java.util.Base64;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//import static java.time.Duration.ofSeconds;
//import static java.util.Objects.isNull;
//import static java.util.Optional.ofNullable;
//import static org.springframework.http.HttpMethod.DELETE;
//import static org.springframework.http.HttpMethod.GET;
//import static org.springframework.http.HttpMethod.POST;
//import static org.springframework.http.HttpMethod.PUT;
//import static reactor.util.retry.Retry.fixedDelay;
//
//@Slf4j
//public abstract class RestClient {
//
//    private WebClient webClient;
//
//    private final WebClient.Builder webClientBuilder;
//
//
//    protected final ObjectReader objectReader;
//
//    protected final ObjectWriter objectWriter;
//
//    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    protected RestClient( WebClient.Builder webClientBuilder) {
//        this.webClientBuilder = webClientBuilder;
//
//        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//        objectReader = objectMapper.reader();
//        objectWriter = objectMapper.writer();
//    }
//
//    @PostConstruct
//    private void webClient() {
//        var clientProperties = clientConfig();
//
//        var httpConnector = getClientHttpConnector(clientProperties.isSslEnabled(), clientProperties);
//
//        final int size = 10 * 1024 * 1024;//10MB
//        final ExchangeStrategies strategies = ExchangeStrategies.builder()
//                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
//                .build();
//
//        webClient = webClientBuilder
//                .baseUrl(clientProperties.getBaseUrl())
//                .clientConnector(httpConnector)
//                .exchangeStrategies(strategies)
//                .build();
//
//        log.info("Web Client created for {}", this.getClass().getSimpleName());
//
//        if (this.getClass().getSimpleName().equalsIgnoreCase("EBXClient") && log.isInfoEnabled()) {
//            log.info("Web client endpoint {}", clientProperties.getBaseUrl());
//        }
//    }
//
//    private ClientHttpConnector getClientHttpConnector(boolean sslEnabled, ClientProperties clientProperties) {
//        ConnectionProvider provider =
//                ConnectionProvider.builder("custom")
//                        .maxConnections(50)
//                        .maxIdleTime(Duration.ofSeconds(20))
//                        .maxLifeTime(Duration.ofSeconds(60))
//                        .pendingAcquireTimeout(Duration.ofSeconds(60))
//                        .evictInBackground(Duration.ofSeconds(120))
//                        .build();
//
//        HttpClient httpClient = HttpClient.create(provider)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getConnectTimeoutMilliSeconds())
//                .doOnConnected(conn -> addHandlerLast(clientProperties, conn)
//                );
//
////        if (sslEnabled) {
////            httpClient = httpClient.secure(getSslProvider(
////                    ddmCertProperties.getKeystorecontent(),
////                    ddmCertProperties.getKeystorepassword())
////            );
////        }
//
//        return new ReactorClientHttpConnector(httpClient);
//    }
//
//    private void addHandlerLast(ClientProperties clientProperties, Connection conn) {
//        if (clientProperties.getReadTimeoutMilliSeconds() > 0) {
//            conn.addHandlerLast(new ReadTimeoutHandler(clientProperties.getReadTimeoutMilliSeconds(), TimeUnit.MILLISECONDS));
//        }
//        if (clientProperties.getWriteTimeoutMilliSeconds() > 0) {
//            conn.addHandlerLast(new WriteTimeoutHandler(clientProperties.getWriteTimeoutMilliSeconds(), TimeUnit.MILLISECONDS));
//        }
//    }
//
//    private SslProvider getSslProvider(String keyStoreBase64, String keyStorePassword) {
//        try (InputStream keyStoreFile = new ByteArrayInputStream(Base64.getDecoder().decode(keyStoreBase64))) {
//
//            var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
//
//            var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
//
//            logCerts(keyStore);
//
//            var sslContext = SslContextBuilder.forClient()
//                    .keyManager(keyManagerFactory)
//                    .build();
//
//            return SslProvider.builder().sslContext(sslContext).build();
//        } catch (Exception e) {
//            log.error("Error creating 2-Way TLS WebClient. Check key-store and trust-store.", e);
//            throw new SpringAIDemoException(SpringAIDemoErrorCode.E2004, e.getMessage());
//        }
//    }
//
//    private void logCerts(KeyStore keyStore) {
//
//        Date today = new Date();
//
//        try {
//            Enumeration<String> aliases = keyStore.aliases();
//            while (aliases.hasMoreElements()) {
//                String alias = aliases.nextElement();
//                Date certExpiryDate = ((X509Certificate) keyStore.getCertificate(alias)).getNotAfter();
//                //Tue Oct 17 06:02:22 AEST 2006
//                long dateDiff = certExpiryDate.getTime() - today.getTime();
//                long expiresIn = dateDiff / (24 * 60 * 60 * 1000);
//                if (log.isInfoEnabled()) {
//                    log.info("Certifiate: {} ; Expires On: {} ; Formated Date: {} ; Today's Date: {} ; Expires In: {}",
//                            alias,
//                            certExpiryDate,
//                            certExpiryDate.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER),
//                            today.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER),
//                            expiresIn);
//                }
//            }
//        } catch (KeyStoreException e) {
//            log.error("", e);
//        }
//    }
//
//    public <T> T get(String uri, Map<String, ?> uriVariables, MultiValueMap<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
//        return execute(GET, uri, uriVariables, queryParams, null, null, responseType);
//    }
//
//    public <I, O> O post(String uri, Map<String, ?> uriVariables, MultiValueMap<String, String> queryParams, Object body, Class<I> requestBodyType, ParameterizedTypeReference<O> responseType) {
//        return execute(POST, uri, uriVariables, queryParams, body, requestBodyType, responseType);
//    }
//
//    public <I, O> O put(String uri, Map<String, ?> uriVariables, MultiValueMap<String, String> queryParams, Object body, Class<I> requestBodyType, ParameterizedTypeReference<O> responseType) {
//        return execute(PUT, uri, uriVariables, queryParams, body, requestBodyType, responseType);
//    }
//
//    public <I, O> O delete(String uri, Map<String, ?> uriVariables, MultiValueMap<String, String> queryParams, Object body, Class<I> requestBodyType, ParameterizedTypeReference<O> responseType) {
//        return execute(DELETE, uri, uriVariables, queryParams, body, requestBodyType, responseType);
//    }
//
//    private <I, O> O execute(HttpMethod httpMethod, String uri, Map<String, ?> uriVariables, MultiValueMap<String, String> queryParams, Object body, Class<I> requestBodyType, ParameterizedTypeReference<O> responseType) {
//        var requestBodySpec = webClient
//                .method(httpMethod)
//                .uri(uriBuilder -> uriBuilder.path(uri).queryParams(queryParams).build(isNull(uriVariables) ? Collections.emptyMap() : uriVariables));
//
//        if (log.isDebugEnabled()) {
//            logRequest(body);
//        }
//
//        O response = addRequestBody(requestBodySpec, body, requestBodyType)
//                .headers(this::accept)
//                .retrieve()
//                .bodyToMono(responseType)
//                .doOnError(this::handleError)
//                .retryWhen(fixedDelay(3, ofSeconds(3)).filter(this::isRetryable))
//                .block();
//
//        if (log.isDebugEnabled()) {
//            logResponse(response);
//        }
//
//        return response;
//    }
//
//    private <I> WebClient.RequestHeadersSpec<?> addRequestBody(WebClient.RequestBodySpec requestBodySpec, Object body, Class<I> requestBodyType) {
//        if (isNull(body)) {
//            log.trace("requestBodyType - {}", requestBodyType);//useless logging, to suppress unused parameter sonar violation, added it
//            return requestBodySpec;
//        }
//
//        return requestBodySpec
//                .bodyValue(body);
//    }
//
//    private void logRequest(Object body) {
//        try {
//            String jsonRequest = objectWriter.writeValueAsString(body);
//            log.debug("requestBody - {}", jsonRequest);
//        } catch (JsonProcessingException e) {
//            log.warn("JSON Parse Error", e);
//        }
//    }
//
//    private <O> void logResponse(O response) {
//        try {
//            String jsonResponse = objectWriter.writeValueAsString(response);
//            log.debug("responseBody - {}", jsonResponse);
//        } catch (JsonProcessingException e) {
//            log.warn("JSON Parse Error", e);
//        }
//    }
//
//    protected void handleError(Throwable error) {
//        String message = ofNullable(error.getMessage()).orElse(error.getClass().getSimpleName());
//
//        log.error("An error occurred {}", message, error);
//
//        if (error instanceof WebClientResponseException) {
//            WebClientResponseException exception = (WebClientResponseException) error;
//            log.info("statusCode - {}, responseBody - {}", exception.getStatusCode(), exception.getResponseBodyAsString());
//            try {
//                var aiDemoErrorCode = objectReader.readValue(exception.getResponseBodyAsString(), SpringAIDemoErrorCode.class);
//                throw new SpringAIDemoException(SpringAIDemoErrorCode.E2004, aiDemoErrorCode.getDefaultMessage());
//            } catch (IOException e) {
//                log.error("JsonProcessingException {}", e.getMessage(), e);
//                throw new SpringAIDemoException(SpringAIDemoErrorCode.E2004, e.getMessage());
//            }
//        }
//    }
//
//    protected abstract ClientProperties clientConfig();
//
//    protected abstract Optional<Map<String, String>> headers();
//
//    protected abstract boolean isRetryable(Throwable throwable);
//
//    private void accept(HttpHeaders httpHeaders) {
//        headers().ifPresent(header -> {
//            for (Map.Entry<String,String> entry : header.entrySet()) {
//                httpHeaders.add(entry.getKey(),entry.getValue());
//            }
//        });
//    }
//}
