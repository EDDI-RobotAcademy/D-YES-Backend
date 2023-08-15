package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.domain.user.service.response.GoogleOauthAccessTokenResponse;
import com.dyes.backend.domain.user.service.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.utility.provider.GoogleOauthSecretsProvider;
import com.dyes.backend.utility.redis.RedisService;
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
    final private GoogleOauthSecretsProvider googleOauthClientIdProvider;
    final private UserRepository userRepository;
    final private RedisService redisService;
    final private RestTemplate restTemplate;

    @Override
    public String googleUserLogin(String code) {
        log.info("googleUserLogin start");

        final GoogleOauthAccessTokenResponse accessTokenResponse = requestAccessTokenWithAuthorizationCode(code);

        ResponseEntity<GoogleOauthUserInfoResponse> userInfoResponse =
                requestUserInfoWithAccessToken(accessTokenResponse.getAccessToken());

        log.info("userInfoResponse: " + userInfoResponse);
        User user = userSave(userInfoResponse.getBody().getId(), accessTokenResponse.getAccessToken(), accessTokenResponse.getRefreshToken());
        log.info("user" + user);

        final String googleUUID = "google" + UUID.randomUUID();
        redisService.setUUIDAndUser(googleUUID, user.getId());

        final String redirectUrl = googleOauthClientIdProvider.getGOOGLE_REDIRECT_URL();
        log.info("googleUserLogin end");

        return redirectUrl + googleUUID;
    }

    public GoogleOauthAccessTokenResponse requestAccessTokenWithAuthorizationCode(String code) {

        log.info("requestAccessToken start");

        final String googleClientId = googleOauthClientIdProvider.getGOOGLE_AUTH_CLIENT_ID();
        log.info("googleClientId: " + googleClientId);

        final String googleRedirectUrl = googleOauthClientIdProvider.getGOOGLE_AUTH_REDIRECT_URL();
        log.info("googleRedirectUrl: " + googleRedirectUrl);

        final String googleClientSecret = googleOauthClientIdProvider.getGOOGLE_AUTH_SECRETS();
        log.info("googleClientSecret: " + googleClientSecret);

        final String googleTokenRequestUrl = googleOauthClientIdProvider.getGOOGLE_TOKEN_REQUEST_URL();
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
    public ResponseEntity<GoogleOauthUserInfoResponse> requestUserInfoWithAccessToken(String AccessToken) {
        log.info("requestUserInfoWithAccessTokenForSignIn start");

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization","Bearer "+ AccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        log.info("request: " + request);
        ResponseEntity<GoogleOauthUserInfoResponse> response = restTemplate.exchange(
                googleOauthClientIdProvider.getGOOGLE_USERINFO_REQUEST_URL(), HttpMethod.GET, request, GoogleOauthUserInfoResponse.class);
        log.info("response: " + response);
        log.info("requestUserInfoWithAccessTokenForSignIn end");
        return response;
    }
    public User userSave (Long id, String accessToken, String refreshToken) {
        log.info("userCheckIsOurUser start");
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) {
            log.info("userCheckIsOurUser OurUser");
            return maybeUser.get();
        } else {
            User user = User.builder()
                    .id(id)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            userRepository.save(user);
            log.info("userCheckIsOurUser NotOurUser");
            return user;
        }
    }
}
