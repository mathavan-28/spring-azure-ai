//package com.rai.online.aidemo.utils;
//
//import com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode;
//import com.rai.online.aidemo.exceptions.SpringAIDemoException;
//import io.netty.handler.timeout.ReadTimeoutException;
//import io.netty.handler.timeout.WriteTimeoutException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.Optional;
//
//import static java.util.Objects.nonNull;
//import static java.util.Optional.ofNullable;
//
//@Slf4j
//@Component
//public class AzureAIClient extends RestClient {
//
//    private final JsonObjectMapper objectMapper = new JsonObjectMapper();
//    private final SpeechClientProperties speechClientProperties;
//
//    public AzureAIClient( SpeechClientProperties speechClientProperties, WebClient.Builder webClientBuilder) {
//        super( webClientBuilder);
//
//        this.speechClientProperties = speechClientProperties;
//    }
//
//    @Override
//    protected ClientProperties clientConfig() {
//        return speechClientProperties;
//    }
//
//    @Override
//    protected Optional<Map<String, String>> headers() {
//        return Optional.of(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
//    }
//
//    @Override
//    protected boolean isRetryable(Throwable throwable) {
//        log.info("{}, {}", throwable.getMessage(), throwable.getClass());
//
//        return throwable instanceof ReadTimeoutException
//                || throwable instanceof WriteTimeoutException
//                || throwable instanceof IOException;
//    }
//
//    @Override
//    protected void handleError(Throwable throwable) {
//        String message = ofNullable(throwable.getMessage()).orElse(throwable.getClass().getSimpleName());
//
//        log.warn("An error occurred {}", message, throwable);
//
//        if (nonNull(throwable.getCause()) && throwable.getCause() instanceof ReadTimeoutException) {
//            throw new SpringAIDemoException(SpringAIDemoErrorCode.E2004,"Connection timeout");
//        } else {
//            if (throwable instanceof WebClientResponseException) {
//                WebClientResponseException exception = (WebClientResponseException) throwable;
//                var responseBodyAsString = exception.getResponseBodyAsString();
//
//                log.info("statusCode - {}, responseBody - {}", exception.getStatusCode(), responseBodyAsString);
//
//                if (exception.getStatusCode().is4xxClientError()) {
//                    var error = objectMapper.readValue(responseBodyAsString, Error.class);
//                    throw new SpringAIDemoException(SpringAIDemoErrorCode.E2004,error.getMessage());
//                } else {
//                    throw new SpringAIDemoException(SpringAIDemoErrorCode.E2004, responseBodyAsString);
//                }
//            } else {
//                super.handleError(throwable);
//            }
//        }
//    }
//}
