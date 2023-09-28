package com.dyes.backend.utility.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource(value = "classpath:application.properties")
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class NaverStmpSecretsProvider {
    @Value("${naver.stmp.email}")
    private String STMP_EMAIL;
    @Value("${naver.stmp.password}")
    private String STMP_PASSWORD;
    @Value("${naver.stmp.link}")
    private String INQUIRY_LINK;
}
