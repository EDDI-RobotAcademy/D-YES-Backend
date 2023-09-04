package com.dyes.backend.domain.authentication.service.google;

import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthAccessTokenResponse;
import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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
@ToString
@RequiredArgsConstructor
public class GoogleAuthenticationServiceImpl implements GoogleAuthenticationService{
    final private GoogleOauthSecretsProvider googleOauthSecretsProvider;
    final private UserRepository userRepository;
    final private RestTemplate restTemplate;

    // 구글 로그인
    @Override
    public GoogleUserLoginRequestForm googleUserLogin(String code) {
        log.info("googleUserLogin start");

        final GoogleOauthAccessTokenResponse tokenResponse = googleRequestAccessTokenWithAuthorizationCode(code);
        ResponseEntity<GoogleOauthUserInfoResponse> userInfoResponse =
                googleRequestUserInfoWithAccessToken(tokenResponse.getAccessToken());

        GoogleUserLoginRequestForm googleUserLoginRequestForm
                = new GoogleUserLoginRequestForm(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                userInfoResponse.getBody().getId(),
                userInfoResponse.getBody().getEmail(),
                userInfoResponse.getBody().getPicture());

        log.info("googleUserLogin end");

        return googleUserLoginRequestForm;
    }

    // 구글에서 인가 코드를 받으면 엑세스 코드 요청
    public GoogleOauthAccessTokenResponse googleRequestAccessTokenWithAuthorizationCode(String code) {
        log.info("requestAccessToken start");

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
        log.info("accessTokenRequest: " + accessTokenResponse);

        if(accessTokenResponse.getStatusCode() == HttpStatus.OK){
            log.info("requestAccessToken end");
            return accessTokenResponse.getBody();
        }
        log.info("requestAccessToken end");

        return null;
    }

    // 구글 엑세스 토큰으로 유저 정보 요청
    @Override
    public ResponseEntity<GoogleOauthUserInfoResponse> googleRequestUserInfoWithAccessToken(String accessToken) {
        log.info("requestUserInfoWithAccessTokenForSignIn start");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization","Bearer "+ accessToken);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                    googleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);
            log.info("response: " + response);
            log.info("requestUserInfoWithAccessTokenForSignIn end");

            return response;
        } catch (RestClientException e) {
            HttpHeaders headers = new HttpHeaders();
            log.error("Error during requestUserInfoWithAccessTokenForSignIn: " + e.getMessage());
            Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);
            User user = maybeUser.get();
            String responseAccessToken = expiredGoogleAccessTokenRequester(user);

            headers.add("Authorization","Bearer "+ responseAccessToken);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                    googleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);
            log.info("response: " + response);
            log.info("requestUserInfoWithAccessTokenForSignIn end");

            return response;
        }
    }

    // 구글 리프래쉬 토큰으로 엑세스 토큰 재발급 받은 후 유저 정보 요청
    @Override
    public String expiredGoogleAccessTokenRequester(User user) {
        log.info("expiredGoogleAccessTokenRequester start");

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

        ResponseEntity<GoogleOauthAccessTokenResponse> accessTokenResponse = restTemplate.postForEntity(googleRefreshTokenRequestUrl, requestEntity, GoogleOauthAccessTokenResponse.class);
        if(accessTokenResponse.getStatusCode() == HttpStatus.OK){
            user.setAccessToken(accessTokenResponse.getBody().getAccessToken());
            userRepository.save(user);
            log.info("expiredGoogleAccessTokenRequester end");
            return user.getAccessToken();
        }
        log.info("expiredGoogleAccessTokenRequester end");
        return null;
    }

    // 구글 회원 Oauth 연결 끊기
    @Override
    public User googleUserDisconnect(User user) throws NullPointerException{
        log.info("user.getAccessToken(): " + user.getAccessToken());

        final String googleRevokeUrl = googleOauthSecretsProvider.getGOOGLE_REVOKE_URL();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", user.getAccessToken());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<JsonNode> jsonNodeResponseEntity = restTemplate.postForEntity(googleRevokeUrl, requestEntity, JsonNode.class);
            JsonNode responseBody = jsonNodeResponseEntity.getBody();
            log.info("jsonNodeResponseEntity: " + responseBody);
            if (responseBody.has("error")) {
                String error = responseBody.get("error").asText();
                String errorDescription = responseBody.get("error_description").asText();

                log.error("Error: " + error + ", Error Description: " + errorDescription);
                return null;
            }
            return user;
        } catch (Exception e){
            log.error("Can't Delete User", e);
            return null;
        }
    }
}
