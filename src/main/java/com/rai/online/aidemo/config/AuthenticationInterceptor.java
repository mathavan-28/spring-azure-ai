//package com.rai.online.aidemo.config;
//
//import com.rai.online.aidemo.exceptions.SpringAIDemoErrorCode;
//import com.rai.online.aidemo.exceptions.SpringAIDemoException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.naming.InvalidNameException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.security.cert.X509Certificate;
//import java.util.stream.Stream;
//
//import static java.util.Objects.nonNull;
//
//@Slf4j
//@Component
//public class AuthenticationInterceptor implements HandlerInterceptor {
//
//
//    private final boolean isDevProfile;
//
//    public AuthenticationInterceptor( @Value("${spring.profiles.active}") String activeProfiles) {
//
//        isDevProfile = Stream.of(activeProfiles.split(",")).map(String::trim).anyMatch(s -> s.equalsIgnoreCase("dev"));
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (isDevProfile) {
//            return true;
//        }
//
//        String xfccHeader = request.getHeader("X-Forwarded-Client-Cert");
//        if (nonNull(xfccHeader)) {
//            X509Certificate[] clientCertificate = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
////            compareCert(clientCertificate[0]);//when there are multiple certs, we get all in array
//            return true;
//        }
//
//        response.sendError(HttpServletResponse.SC_FORBIDDEN);
//
//        throw new SpringAIDemoException(SpringAIDemoErrorCode.E2003, "No certificate provided");
//    }
////
////    private void compareCert(X509Certificate parsedCertificate) throws InvalidNameException {
////        String callerCN = CertificateUtil.getCN(parsedCertificate);
////
////        String caller = authorizationProperties.getCns().stream()
////                .filter(cn -> cn.equals(callerCN))
////                .findFirst()
////                .orElseThrow(() -> {
////                    log.error("Certificate is not trusted, certificate details: " + callerCN);
////                    log.error("Provided certificate: " + parsedCertificate);
////                    SecurityEvent securityEvent = DDDSecurityEvents.certificateNotTrustedEvents(callerCN);
////                    SecurityLogging.logSecurityEvent(securityEvent);
////                    return new ForbiddenException(DDDErrorCode.E2020, "CN is not trusted");
////                });
////
////        log.info("caller - {}", caller);
////    }
//}
