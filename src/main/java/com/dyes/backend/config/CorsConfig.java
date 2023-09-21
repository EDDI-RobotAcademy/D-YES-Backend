package com.dyes.backend.config;

import com.dyes.backend.utility.PropertyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final PropertyUtil propertyUtil;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        final String ALLOWED_ORIGINS = propertyUtil.getProperty("allowed_origins");

        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders(HttpHeaders.AUTHORIZATION)
                .allowCredentials(true);
    }
}
