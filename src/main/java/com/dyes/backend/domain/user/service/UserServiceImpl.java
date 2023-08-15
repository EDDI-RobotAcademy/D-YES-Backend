package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserProfile;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.response.GoogleOauthAccessTokenResponse;
import com.dyes.backend.domain.user.service.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.user.service.response.NaverOauthAccessTokenResponse;
import com.dyes.backend.domain.user.service.response.NaverOauthUserInfoResponse;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.dyes.backend.utility.provider.NaverOauthSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final private GoogleOauthSecretsProvider googleOauthSecretsProvider;
    final private NaverOauthSecretsProvider naverOauthSecretsProvider;
    final private UserRepository userRepository;
    final private UserProfileRepository userProfileRepository;
    final private RedisService redisService;
    final private RestTemplate restTemplate;
    final private ObjectMapper objectMapper;

    // 구글 로그인
    @Override
    public String googleUserLogin(String code) {
        log.info("googleUserLogin start");

        final GoogleOauthAccessTokenResponse accessTokenResponse = googleRequestAccessTokenWithAuthorizationCode(code);

        ResponseEntity<GoogleOauthUserInfoResponse> userInfoResponse =
                googleRequestUserInfoWithAccessToken(accessTokenResponse.getAccessToken());

        log.info("userInfoResponse: " + userInfoResponse);
        User user = googleUserSave(accessTokenResponse, userInfoResponse.getBody());
        log.info("user" + user);

        final String userToken = "google" + UUID.randomUUID();
        redisService.setUserTokenAndUser(userToken, user.getId());

        final String redirectUrl = googleOauthSecretsProvider.getGOOGLE_REDIRECT_VIEW_URL();
        log.info("googleUserLogin end");

        return redirectUrl + userToken;
    }
    // 구글에서 인가 코드를 받으면 엑세스 코드 요청
    public GoogleOauthAccessTokenResponse googleRequestAccessTokenWithAuthorizationCode(String code) {

        log.info("requestAccessToken start");

        final String googleClientId = googleOauthSecretsProvider.getGOOGLE_AUTH_CLIENT_ID();
        log.info("googleClientId: " + googleClientId);

        final String googleRedirectUrl = googleOauthSecretsProvider.getGOOGLE_AUTH_REDIRECT_URL();
        log.info("googleRedirectUrl: " + googleRedirectUrl);

        final String googleClientSecret = googleOauthSecretsProvider.getGOOGLE_AUTH_SECRETS();
        log.info("googleClientSecret: " + googleClientSecret);

        final String googleTokenRequestUrl = googleOauthSecretsProvider.getGOOGLE_TOKEN_REQUEST_URL();
        log.info("googleTokenRequestUrl: " + googleTokenRequestUrl);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("code", code);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUrl);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<GoogleOauthAccessTokenResponse> accessTokenResponse = restTemplate.postForEntity(googleTokenRequestUrl, requestEntity, GoogleOauthAccessTokenResponse.class);
        log.info("accessTokenRequest: " + accessTokenResponse);

        if(accessTokenResponse.getStatusCode() == HttpStatus.OK){
            return accessTokenResponse.getBody();
        }
        log.info("requestAccessToken end");

        return null;
    }
    // 구글 엑세스 코드로 유저 정보 요청
    public ResponseEntity<GoogleOauthUserInfoResponse> googleRequestUserInfoWithAccessToken(String AccessToken) {
        log.info("requestUserInfoWithAccessTokenForSignIn start");

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization","Bearer "+ AccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        log.info("request: " + request);
        ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                googleOauthSecretsProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);
        log.info("response: " + response);
        log.info("requestUserInfoWithAccessTokenForSignIn end");
        return response;
    }
    public User googleUserSave (GoogleOauthAccessTokenResponse accessTokenResponse, GoogleOauthUserInfoResponse userInfoResponse) {
        log.info("userCheckIsOurUser start");
        Optional<User> maybeUser = userRepository.findByStringId(userInfoResponse.getId());
        if (maybeUser.isPresent()) {
            log.info("userCheckIsOurUser OurUser");
            return maybeUser.get();
        } else {
            User user = User.builder()
                    .id(userInfoResponse.getId())
                    .accessToken(accessTokenResponse.getAccessToken())
                    .refreshToken(accessTokenResponse.getRefreshToken())
                    .build();
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(userInfoResponse.getId())
                    .email(userInfoResponse.getEmail())
                    .profileImg(userInfoResponse.getPicture())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("userCheckIsOurUser NotOurUser");

            return user;
        }
    }

    /*
    <------------------------------------------------------------------------------------------------------------------>
     */
    // 네이버 로그인
    @Override
    public String naverUserLogin(String code) {
        log.info("naverUserLogin start");

        final NaverOauthAccessTokenResponse accessTokenResponse = naverRequestAccessTokenWithAuthorizationCode(code);

        NaverOauthUserInfoResponse userInfoResponse =
                naverRequestUserInfoWithAccessToken(accessTokenResponse.getAccessToken());

        log.info("userInfoResponse: " + userInfoResponse);
        User user = naverUserSave(accessTokenResponse, userInfoResponse);
        log.info("user" + user);

        final String userToken = "naver" + UUID.randomUUID();
        redisService.setUserTokenAndUser(userToken, user.getId());

        final String redirectUrl = naverOauthSecretsProvider.getNAVER_REDIRECT_VIEW_URL();
        log.info("naverUserLogin end");

        return redirectUrl + userToken;
    }

    // 네이버에서 인가 코드를 받으면 엑세스 코드 요청
    public NaverOauthAccessTokenResponse naverRequestAccessTokenWithAuthorizationCode(String code) {

        log.info("requestAccessToken start");

        final String naverClientId = naverOauthSecretsProvider.getNAVER_AUTH_CLIENT_ID();
        log.info("naverClientId: " + naverClientId);

        final String naverRedirectUrl = naverOauthSecretsProvider.getNAVER_AUTH_REDIRECT_URL();
        log.info("naverRedirectUrl: " + naverRedirectUrl);

        final String naverClientSecret = naverOauthSecretsProvider.getNAVER_AUTH_SECRETS();
        log.info("naverClientSecret: " + naverClientSecret);

        final String naverTokenRequestUrl = naverOauthSecretsProvider.getNAVER_TOKEN_REQUEST_URL();
        log.info("naverTokenRequestUrl: " + naverTokenRequestUrl);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("code", code);
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("redirect_uri", naverRedirectUrl);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<NaverOauthAccessTokenResponse> accessTokenResponse = restTemplate.postForEntity(naverTokenRequestUrl, requestEntity, NaverOauthAccessTokenResponse.class);
        log.info("accessTokenRequest: " + accessTokenResponse);

        if(accessTokenResponse.getStatusCode() == HttpStatus.OK){
            return accessTokenResponse.getBody();
        }
        log.info("requestAccessToken end");

        return null;
    }

    // 네이버 엑세스 코드로 유저 정보 요청
    public NaverOauthUserInfoResponse naverRequestUserInfoWithAccessToken(String AccessToken) {
        log.info("requestUserInfoWithAccessTokenForSignIn start");

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization","Bearer "+ AccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        log.info("request: " + request);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
        naverOauthSecretsProvider.getNAVER_USERINFO_REQUEST_URL(), HttpMethod.GET, request, JsonNode.class);
        log.info("response: " + response);

        JsonNode responseNode = response.getBody().get("response");
        log.info("Raw JSON response: " + responseNode);

        NaverOauthUserInfoResponse userInfoResponse =
                objectMapper.convertValue(responseNode, NaverOauthUserInfoResponse.class);
        log.info("Parsed response: " + userInfoResponse);

        return userInfoResponse;
    }

    public User naverUserSave (NaverOauthAccessTokenResponse accessTokenResponse, NaverOauthUserInfoResponse userInfoResponse) {
        log.info("userCheckIsOurUser start");
        Optional<User> maybeUser = userRepository.findByStringId(userInfoResponse.getId());
        if (maybeUser.isPresent()) {
            log.info("userCheckIsOurUser OurUser");
            return maybeUser.get();
        } else {
            User user = User.builder()
                    .id(userInfoResponse.getId())
                    .accessToken(accessTokenResponse.getAccessToken())
                    .refreshToken(accessTokenResponse.getRefreshToken())
                    .build();
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .id(userInfoResponse.getId())
                    .contactNumber(userInfoResponse.getMobile_e164())
                    .email(userInfoResponse.getEmail())
                    .profileImg(userInfoResponse.getProfile_image())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("userCheckIsOurUser NotOurUser");
            return user;
        }
    }
}
