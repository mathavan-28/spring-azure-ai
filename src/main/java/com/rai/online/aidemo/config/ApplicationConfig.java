//package com.rai.online.aidemo.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.context.event.EventListener;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Slf4j
//@Configuration
//@EnableAspectJAutoProxy(exposeProxy = true)
//public class ApplicationConfig implements WebMvcConfigurer {
//
//    private final AuthenticationInterceptor authenticationInterceptor;
//
//    public ApplicationConfig(AuthenticationInterceptor authenticationInterceptor ) {
//        this.authenticationInterceptor = authenticationInterceptor;
//
//    }
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**").excludePathPatterns("/actuator/**");
//
//    }
//
//    @EventListener(RefreshScopeRefreshedEvent.class)
//    public void onRefresh(RefreshScopeRefreshedEvent event) {
//        log.info("Property refreshed from config server - {}", event.getName());
//    }
//
//}
