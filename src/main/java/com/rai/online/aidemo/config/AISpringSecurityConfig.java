package com.rai.online.aidemo.config;

import com.rai.online.aidemo.model.AIResponseTypeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AISpringSecurityConfig implements WebMvcConfigurer {

    public static final String AZURE_AI_VASSIST_RESPONSE_TYPE = "azureAiVassistResponseType";

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh(RefreshScopeRefreshedEvent event) {
        log.info("Property refreshed from config server");
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(10485760);//10 MB
        return loggingFilter;
    }

    //    @Bean("inMemCacheManager")
//    public CacheManager inMemCacheManager() {
//        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
//        cacheManager.setCacheNames(List.of(AZURE_AI_VASSIST_RESPONSE_TYPE));
//        return cacheManager;
//    }
    @Bean
    @ApplicationScope
    public AIResponseTypeGenerator sessionScopedResponseBean() {
        return new AIResponseTypeGenerator();
    }
}

