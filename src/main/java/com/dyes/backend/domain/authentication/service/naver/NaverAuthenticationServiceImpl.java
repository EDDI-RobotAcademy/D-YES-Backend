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
public class NaverAuthenticationServiceImpl implements NaverAuthenticationService{
    final private NaverOauthSecretsProvider naverOauthSecretsProvider;
    final private UserRepository userRepository;
    final private RestTemplate restTemplate;
    final private ObjectMapper objectMapper;

    // 네이버 로그인
    @Override
    public NaverUserLoginRequestForm naverUserLogin(String code) {
        log.info("naverUserLogin start");

        final NaverOauthAccessTokenResponse tokenResponse = naverRequestAccessTokenWithAuthorizationCode(code);

        NaverOauthUserInfoResponse userInfoResponse =
                naverRequestUserInfoWithAccessToken(tokenResponse.getAccessToken());

        NaverUserLoginRequestForm naverUserLoginRequestForm
                = new NaverUserLoginRequestForm(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                userInfoResponse.getId(),
                userInfoResponse.getMobile_e164(),
                userInfoResponse.getEmail(),
                userInfoResponse.getProfile_image());

        log.info("naverUserLogin end");

        return naverUserLoginRequestForm;
    }

    // 네이버에서 인가 코드를 받으면 엑세스 코드 요청
    public NaverOauthAccessTokenResponse naverRequestAccessTokenWithAuthorizationCode(String code) {
        log.info("requestAccessToken start");

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

        if(accessTokenResponse.getStatusCode() == HttpStatus.OK){
            log.info("requestAccessToken end");
            return accessTokenResponse.getBody();
        }
        log.info("requestAccessToken end");
        return null;
    }

    // 네이버 엑세스 토큰으로 유저 정보 요청
    @Override
    public NaverOauthUserInfoResponse naverRequestUserInfoWithAccessToken(String accessToken) {
        log.info("requestUserInfoWithAccessTokenForSignIn start");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization","Bearer "+ accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    naverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL(), HttpMethod.GET, request, JsonNode.class);
            log.info("response: " + response);

            JsonNode responseNode = response.getBody().get("response");

            NaverOauthUserInfoResponse userInfoResponse =
                    objectMapper.convertValue(responseNode, NaverOauthUserInfoResponse.class);

            log.info("requestUserInfoWithAccessTokenForSignIn end");

            return userInfoResponse;
        } catch (RestClientException e) {
            HttpHeaders headers = new HttpHeaders();
            log.error("Error during requestUserInfoWithAccessTokenForSignIn: " + e.getMessage());
            Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);
            User user = maybeUser.get();
            String responseAccessToken = expiredNaverAccessTokenRequester(user);
            headers.add("Authorization","Bearer "+ responseAccessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
            log.info("request: " + request);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    naverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL(), HttpMethod.GET, request, JsonNode.class);
            log.info("response: " + response);

            JsonNode responseNode = response.getBody().get("response");

            NaverOauthUserInfoResponse userInfoResponse =
                    objectMapper.convertValue(responseNode, NaverOauthUserInfoResponse.class);

            log.info("requestUserInfoWithAccessTokenForSignIn end");

            return userInfoResponse;
        }

    }

    // 네이버 리프래쉬 토큰으로 엑세스 토큰 재발급 받은 후 유저 정보 요청
    @Override
    public String expiredNaverAccessTokenRequester(User user) {
        log.info("expiredNaverAccessTokenRequester start");

        String refreshToken = user.getRefreshToken();
        log.info("기존 액세스 : " + user.getAccessToken());
        log.info("기존 리프래쉬 : " + user.getRefreshToken());

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

        ResponseEntity<NaverOauthAccessTokenResponse> accessTokenResponse = restTemplate.postForEntity(naverRefreshTokenRequestUrl, requestEntity, NaverOauthAccessTokenResponse.class);
        log.info("새로운 액세스 : " + accessTokenResponse.getBody().getAccessToken());
        log.info("새로운 리프래쉬 : " + accessTokenResponse.getBody().getRefreshToken());
        log.info("accessTokenResponse확인용 : " + accessTokenResponse.getBody());
        if(accessTokenResponse.getStatusCode() == HttpStatus.OK){
            user.setAccessToken(accessTokenResponse.getBody().getAccessToken());
            userRepository.save(user);
            log.info("DB에 변경된 액세스 : " + user.getAccessToken());
            log.info("DB에 변경된 리프래쉬 : " + user.getRefreshToken());
            log.info("expiredNaverAccessTokenRequester end");

            return user.getAccessToken();
        }
        log.info("expiredNaverAccessTokenRequester end");
        return null;
    }

    // 네이버 회원 Oauth 연결 끊기
    @Override
    public User naverUserDisconnect(User user) throws NullPointerException{
        log.info("user.getAccessToken(): " + user.getAccessToken());

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
            ResponseEntity<JsonNode> jsonNodeResponseEntity = restTemplate.postForEntity(naverRevokeUrl, requestEntity, JsonNode.class);
            JsonNode responseBody = jsonNodeResponseEntity.getBody();
            log.info("jsonNodeResponseEntity: " + responseBody);
            if (responseBody.has("error")) {
                String error = responseBody.get("error").asText();
                String errorDescription = responseBody.get("error_description").asText();

                log.error("Error: " + error + ", Error Description: " + errorDescription);
                return null;
            }
            return user;
        }catch (Exception e){
            log.error("Can't Delete User", e);
            return null;
        }
    }
}
