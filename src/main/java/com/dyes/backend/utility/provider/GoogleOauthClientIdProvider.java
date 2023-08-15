package com.dyes.backend.utility.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource(value = "classpath:application.properties")
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOauthClientIdProvider {
    @Value("${google.oauth.client.id}")
    private String GOOGLE_AUTH_CLIENT_ID;
    @Value("${google.oauth.redirect.url}")
    private String GOOGLE_AUTH_REDIRECT_URL;
    @Value("${google.oauth.secrets}")
    private String GOOGLE_AUTH_SECRETS;
    @Value("${google.oauth.token.request.url}")
    private String GOOGLE_TOKEN_REQUEST_URL;
    @Value("${google.oauth.user.info.request.url}")
    private String GOOGLE_USERINFO_REQUEST_URL;
}
