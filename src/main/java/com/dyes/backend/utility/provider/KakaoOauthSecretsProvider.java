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
public class KakaoOauthSecretsProvider {
    @Value("${kakao.oauth.restapi.key}")
    private String KAKAO_AUTH_RESTAPI_KEY;
    @Value("${kakao.oauth.redirect.url}")
    private String KAKAO_AUTH_REDIRECT_URL;
    @Value("${kakao.login.redirect.view}")
    private String KAKAO_REDIRECT_VIEW_URL;
    @Value("${kakao.oauth.token.request.url}")
    private String KAKAO_TOKEN_REQUEST_URL;
    @Value("${kakao.oauth.user.info.request.url}")
    private String KAKAO_USERINFO_REQUEST_URL;
    @Value("${kakao.oauth.refresh.token.request}")
    private String KAKAO_REFRESH_TOKEN_REQUEST_URL;
}
