package com.rabo.online.aidemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@Configuration
@EnableWebMvc
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {



    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) {
        try {
            http
                    .authorizeRequests()
                    .antMatchers("/actuator/health").permitAll()
                    .antMatchers("/actuator/**").authenticated()
                    .and().httpBasic();
            http.cors();
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/**")
                    .anonymous()
                    .anyRequest().authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//            http
//                    .csrf().disable()
//                    .cors().disable()
//                    .authorizeRequests()
//                    .antMatchers("/welcome")
//                    .permitAll()
//                    .anyRequest()
//                    .authenticated().and()
//                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                    .and().headers().frameOptions().disable()
//                    .and().headers().xssProtection().disable();
//                    .and().headers().contentSecurityPolicy("default-src 'self'");
        } catch (Exception e) {
            log.error("SpringAiDemoException Initialization Failed", e);
//            throw new SpringAiDemoException( "Actuator Initialization failed");
        }
    }

}
