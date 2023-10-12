package com.dyes.backend.domain.authentication.service.google;

import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthAccessTokenResponse;
import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleAuthenticationServiceImpl implements GoogleAuthenticationService {
    final private GoogleOauthSecretsProvider googleOauthSecretsProvider;
    final private UserRepository userRepository;
    final private RestTemplate restTemplate;

    // 구글 로그인
    @Override
    public GoogleUserLoginRequestForm googleLogin(String code) {
        log.info("googleLogin start");

        final GoogleOauthAccessTokenResponse tokenResponse = requestAccessTokenFromGoogle(code);

        ResponseEntity<GoogleOauthUserInfoResponse> userInfoResponse =
                requestUserInfoFromGoogle(tokenResponse.getAccessToken());

        GoogleUserLoginRequestForm googleUserLoginRequestForm
                = new GoogleUserLoginRequestForm(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                userInfoResponse.getBody().getId(),
                userInfoResponse.getBody().getEmail(),
                userInfoResponse.getBody().getPicture());

        log.info("googleLogin end");
        return googleUserLoginRequestForm;
    }

    // 구글에서 인가 코드를 받으면 엑세스 토큰 요청
    public GoogleOauthAccessTokenResponse requestAccessTokenFromGoogle(String code) {
        log.info("requestAccessTokenFromGoogle start");

        final String googleClientId = googleOauthSecretsProvider.getGOOGLE_AUTH_CLIENT_ID();
        final String googleRedirectUrl = googleOauthSecretsProvider.getGOOGLE_AUTH_REDIRECT_URL();
        final String googleClientSecret = googleOauthSecretsProvider.getGOOGLE_AUTH_SECRETS();
        final String googleTokenRequestUrl = googleOauthSecretsProvider.getGOOGLE_TOKEN_REQUEST_URL();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("code", code);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUrl);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<GoogleOauthAccessTokenResponse> accessTokenResponse
                = restTemplate.postForEntity(googleTokenRequestUrl, requestEntity, GoogleOauthAccessTokenResponse.class);
        log.debug("accessTokenRequest: " + accessTokenResponse);

        if (accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            log.info("requestAccessTokenFromGoogle end");
            return accessTokenResponse.getBody();
        }

        log.info("requestAccessTokenFromGoogle end");
        return null;
    }

    // 구글 액세스 토큰으로 유저 정보 요청
    @Override
    public ResponseEntity<GoogleOauthUserInfoResponse> requestUserInfoFromGoogle(String accessToken) {
        log.info("requestUserInfoFromGoogle start");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                    googleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);

            log.info("response: " + response);
            log.info("requestUserInfoFromGoogle end");

            return response;
        } catch (RestClientException e) {
            HttpHeaders headers = new HttpHeaders();
            log.error("Error during requestUserInfoFromGoogle: " + e.getMessage());

            Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);
            User user = maybeUser.get();
            String responseAccessToken = refreshGoogleAccessToken(user);

            headers.add("Authorization", "Bearer " + responseAccessToken);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                    googleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);
            log.info("response: " + response);
            log.info("requestUserInfoFromGoogle end");

            return response;
        }
    }

    // 구글 리프래쉬 토큰으로 액세스 토큰 재발급
    @Override
    public String refreshGoogleAccessToken(User user) {
        log.info("refreshGoogleAccessToken start");

        String refreshToken = user.getRefreshToken();

        final String googleClientId = googleOauthSecretsProvider.getGOOGLE_AUTH_CLIENT_ID();
        final String googleClientSecret = googleOauthSecretsProvider.getGOOGLE_AUTH_SECRETS();
        final String googleRefreshTokenRequestUrl = googleOauthSecretsProvider.getGOOGLE_REFRESH_TOKEN_REQUEST_URL();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("refresh_token", refreshToken);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<GoogleOauthAccessTokenResponse> accessTokenResponse
                = restTemplate.postForEntity(googleRefreshTokenRequestUrl, requestEntity, GoogleOauthAccessTokenResponse.class);
        log.info("new accessToken : " + accessTokenResponse.getBody().getAccessToken());
        log.info("new refreshToken : " + accessTokenResponse.getBody().getRefreshToken());

        if (accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            user.updateAccessToken(accessTokenResponse.getBody().getAccessToken());
            userRepository.save(user);

            log.info("Changed accessToken in the database : " + user.getAccessToken());
            log.info("Changed refreshToken in the database : " + user.getRefreshToken());

            log.info("refreshGoogleAccessToken end");
            return user.getAccessToken();
        }

        log.info("refreshGoogleAccessToken end");
        return null;
    }

    // 구글 회원 Oauth 연결 끊기
    @Override
    public User disconnectGoogleUser(User user) throws NullPointerException {
        log.info("disconnectGoogleUser start");

        final String googleRevokeUrl = googleOauthSecretsProvider.getGOOGLE_REVOKE_URL();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", user.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> jsonNodeResponseEntity
                    = restTemplate.postForEntity(googleRevokeUrl, requestEntity, JsonNode.class);
            JsonNode responseBody = jsonNodeResponseEntity.getBody();
            log.info("jsonNodeResponseEntity: " + responseBody);

            if (responseBody.has("error")) {
                String error = responseBody.get("error").asText();
                String errorDescription = responseBody.get("error_description").asText();

                log.error("Error: " + error + ", Error Description: " + errorDescription);
                log.info("disconnectGoogleUser end");
                return null;
            }

            return user;
        } catch (Exception e) {
            log.error("Unable to disconnect Google user", e);
            log.info("disconnectGoogleUser end");
            return null;
        }
    }
}
