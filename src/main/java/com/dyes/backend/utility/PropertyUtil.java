package com.dyes.backend.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySources({
        @PropertySource("classpath:cors.properties"),
        @PropertySource("classpath:redis.properties"),
        @PropertySource("classpath:mysql.properties")
})
public class PropertyUtil {

    final private Environment environment;

    public String getProperty(String key) {
        return environment.getProperty(key);
    }
}
