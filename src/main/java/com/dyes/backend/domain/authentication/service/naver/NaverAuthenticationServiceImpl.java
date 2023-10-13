package com.dyes.backend.domain.authentication.service.naver;

import com.dyes.backend.domain.authentication.service.naver.response.NaverOauthAccessTokenResponse;
import com.dyes.backend.domain.authentication.service.naver.response.NaverOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.provider.NaverOauthSecretsProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class NaverAuthenticationServiceImpl implements NaverAuthenticationService {
    final private NaverOauthSecretsProvider naverOauthSecretsProvider;
    final private UserRepository userRepository;
    final private RestTemplate restTemplate;
    final private ObjectMapper objectMapper;

    // 네이버 로그인
    @Override
    public NaverUserLoginRequestForm naverLogin(String code) {
        log.info("naverLogin start");

        final NaverOauthAccessTokenResponse tokenResponse = requestAccessTokenFromNaver(code);

        NaverOauthUserInfoResponse userInfoResponse =
                requestUserInfoFromNaver(tokenResponse.getAccessToken());

        NaverUserLoginRequestForm naverUserLoginRequestForm
                = new NaverUserLoginRequestForm(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                userInfoResponse.getId(),
                userInfoResponse.getMobile_e164(),
                userInfoResponse.getEmail(),
                userInfoResponse.getProfile_image());

        log.info("naverLogin end");
        return naverUserLoginRequestForm;
    }

    // 네이버에서 인가 코드를 받으면 액세스 토큰 요청
    public NaverOauthAccessTokenResponse requestAccessTokenFromNaver(String code) {
        log.info("requestAccessTokenFromNaver start");

        final String naverClientId = naverOauthSecretsProvider.getNAVER_AUTH_CLIENT_ID();
        final String naverRedirectUrl = naverOauthSecretsProvider.getNAVER_AUTH_REDIRECT_URL();
        final String naverClientSecret = naverOauthSecretsProvider.getNAVER_AUTH_SECRETS();
        final String naverTokenRequestUrl = naverOauthSecretsProvider.getNAVER_TOKEN_REQUEST_URL();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("code", code);
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("redirect_uri", naverRedirectUrl);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<NaverOauthAccessTokenResponse> accessTokenResponse
                = restTemplate.postForEntity(naverTokenRequestUrl, requestEntity, NaverOauthAccessTokenResponse.class);
        log.info("accessTokenRequest: " + accessTokenResponse);

        if (accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            log.info("requestAccessTokenFromNaver end");
            return accessTokenResponse.getBody();
        }
        log.info("requestAccessTokenFromNaver end");
        return null;
    }

    // 네이버 액세스 토큰으로 유저 정보 요청
    @Override
    public NaverOauthUserInfoResponse requestUserInfoFromNaver(String accessToken) {
        log.info("requestUserInfoFromNaver start");

        final String naverUserInfoRequestUrl = naverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<JsonNode> response
                    = restTemplate.exchange(naverUserInfoRequestUrl, HttpMethod.GET, request, JsonNode.class);
            log.info("response: " + response);

            JsonNode responseNode = response.getBody().get("response");

            NaverOauthUserInfoResponse userInfoResponse =
                    objectMapper.convertValue(responseNode, NaverOauthUserInfoResponse.class);

            log.info("requestUserInfoFromNaver end");
            return userInfoResponse;
        } catch (RestClientException e) {
            HttpHeaders headers = new HttpHeaders();
            log.error("Error during requestUserInfoFromNaver: " + e.getMessage());

            Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);
            User user = maybeUser.get();

            String responseAccessToken = refreshNaverAccessToken(user);
            headers.add("Authorization", "Bearer " + responseAccessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<JsonNode> response
                    = restTemplate.exchange(naverUserInfoRequestUrl, HttpMethod.GET, request, JsonNode.class);
            log.info("response: " + response);

            JsonNode responseNode = response.getBody().get("response");

            NaverOauthUserInfoResponse userInfoResponse =
                    objectMapper.convertValue(responseNode, NaverOauthUserInfoResponse.class);

            log.info("requestUserInfoFromNaver end");
            return userInfoResponse;
        }
    }

    // 네이버 리프래쉬 토큰으로 액세스 토큰 재발급
    @Override
    public String refreshNaverAccessToken(User user) {
        log.info("refreshNaverAccessToken start");

        String refreshToken = user.getRefreshToken();

        final String naverClientId = naverOauthSecretsProvider.getNAVER_AUTH_CLIENT_ID();
        final String naverClientSecret = naverOauthSecretsProvider.getNAVER_AUTH_SECRETS();
        final String naverRefreshTokenRequestUrl = naverOauthSecretsProvider.getNAVER_REFRESH_TOKEN_REQUEST_URL();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("refresh_token", refreshToken);
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<NaverOauthAccessTokenResponse> accessTokenResponse
                = restTemplate.postForEntity(naverRefreshTokenRequestUrl, requestEntity, NaverOauthAccessTokenResponse.class);
        log.info("new accessToken : " + accessTokenResponse.getBody().getAccessToken());
        log.info("new refreshToken : " + accessTokenResponse.getBody().getRefreshToken());

        if (accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            user.updateAccessToken(accessTokenResponse.getBody().getAccessToken());
            userRepository.save(user);

            log.info("Changed accessToken in the database : " + user.getAccessToken());
            log.info("Changed refreshToken in the database : " + user.getRefreshToken());

            log.info("refreshNaverAccessToken end");
            return user.getAccessToken();
        }
        log.info("refreshNaverAccessToken end");
        return null;
    }

    // 네이버 회원 Oauth 연결 끊기
    @Override
    public User disconnectNaverUser(User user) throws NullPointerException {
        log.info("disconnectNaverUser start");

        String accessToken = user.getAccessToken();

        final String naverRevokeUrl = naverOauthSecretsProvider.getNAVER_REVOKE_URL();
        final String naverClientId = naverOauthSecretsProvider.getNAVER_AUTH_CLIENT_ID();
        final String naverSecrets = naverOauthSecretsProvider.getNAVER_AUTH_SECRETS();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("access_token", accessToken);
        body.add("client_id", naverClientId);
        body.add("client_secret", naverSecrets);
        body.add("grant_type", "delete");
        body.add("service_provider", "NAVER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> jsonNodeResponseEntity
                    = restTemplate.postForEntity(naverRevokeUrl, requestEntity, JsonNode.class);
            JsonNode responseBody = jsonNodeResponseEntity.getBody();

            log.info("jsonNodeResponseEntity: " + responseBody);

            if (responseBody.has("error")) {
                String error = responseBody.get("error").asText();
                String errorDescription = responseBody.get("error_description").asText();

                log.error("Error: " + error + ", Error Description: " + errorDescription);
                log.info("disconnectNaverUser end");
                return null;
            }
            return user;
        } catch (Exception e) {
            log.error("Unable to disconnect Naver user", e);
            log.info("disconnectNaverUser end");
            return null;
        }
    }
}
