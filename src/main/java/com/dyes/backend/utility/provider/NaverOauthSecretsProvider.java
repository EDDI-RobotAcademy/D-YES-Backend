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
public class NaverOauthSecretsProvider {
        @Value("${naver.oauth.client.id}")
        private String NAVER_AUTH_CLIENT_ID;
        @Value("${naver.oauth.redirect.url}")
        private String NAVER_AUTH_REDIRECT_URL;
        @Value("${naver.oauth.secrets}")
        private String NAVER_AUTH_SECRETS;
        @Value("${naver.oauth.token.request.url}")
        private String NAVER_TOKEN_REQUEST_URL;
        @Value("${naver.oauth.user.info.request.url}")
        private String NAVER_USERINFO_REQUEST_URL;
        @Value("${naver.login.redirect.view}")
        private String NAVER_REDIRECT_VIEW_URL;

}
