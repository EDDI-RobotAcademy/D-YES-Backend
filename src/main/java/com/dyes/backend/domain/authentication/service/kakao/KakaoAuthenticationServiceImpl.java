package com.dyes.backend.domain.authentication.service.kakao;

import com.dyes.backend.domain.authentication.service.kakao.response.KakaoOauthAccessTokenResponse;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoOauthDisconnectUserInfoResponse;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.provider.KakaoOauthSecretsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.dyes.backend.utility.common.CommonUtils.setHeaders;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {
    final private KakaoOauthSecretsProvider kakaoOauthSecretsProvider;
    final private UserRepository userRepository;
    final private RestTemplate restTemplate;

    // 카카오 로그인
    @Override
    public KakaoUserLoginRequestForm kakaoLogin(String code) {
        log.info("kakaoLogin start");

        // 카카오 서버에서 accessToken 받아오기
        KakaoOauthAccessTokenResponse kakaoOauthAccessTokenResponse = requestAccessTokenFromKakao(code);
        final String accessToken = kakaoOauthAccessTokenResponse.getAccess_token();
        final String refreshToken = kakaoOauthAccessTokenResponse.getRefresh_token();

        log.info("kakao accessToken: " + accessToken);
        log.info("kakao refreshToken: " + refreshToken);

        // 카카오 서버에서 받아온 accessToken으로 사용자 정보 받아오기
        KakaoOauthUserInfoResponse kakaoOauthUserInfoResponse = requestUserInfoFromKakao(accessToken);

        KakaoUserLoginRequestForm kakaoUserLoginRequestForm
                = new KakaoUserLoginRequestForm(
                        kakaoOauthAccessTokenResponse.getAccess_token(),
                        kakaoOauthAccessTokenResponse.getRefresh_token(),
                        kakaoOauthUserInfoResponse.getId(),
                        kakaoOauthUserInfoResponse.getProperties().getNickname(),
                        kakaoOauthUserInfoResponse.getProperties().getProfile_image());

        log.info("kakaoLogin end");
        return kakaoUserLoginRequestForm;
    }

    // 카카오에서 인가 코드를 받으면 액세스 토큰 요청
    public KakaoOauthAccessTokenResponse requestAccessTokenFromKakao(String code) {
        log.info("requestAccessTokenFromKakao start");

        final String kakaoClientId = kakaoOauthSecretsProvider.getKAKAO_AUTH_RESTAPI_KEY();
        final String kakaoRedirectUrl = kakaoOauthSecretsProvider.getKAKAO_AUTH_REDIRECT_URL();
        final String kakaoTokenRequestUrl = kakaoOauthSecretsProvider.getKAKAO_TOKEN_REQUEST_URL();

        // 헤더 설정
        HttpHeaders httpHeaders = setHeaders();

        // 바디 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", kakaoClientId);
        parameters.add("redirect_uri", kakaoRedirectUrl);
        parameters.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        ResponseEntity<KakaoOauthAccessTokenResponse> kakaoAccessTokenResponseForm
                = restTemplate.postForEntity(kakaoTokenRequestUrl, requestEntity, KakaoOauthAccessTokenResponse.class);

        log.info("requestAccessTokenFromKakao end");
        return kakaoAccessTokenResponseForm.getBody();
    }

    // 카카오 액세스 토큰으로 유저 정보 요청
    @Override
    public KakaoOauthUserInfoResponse requestUserInfoFromKakao(String accessToken) {
        log.info("requestUserInfoFromKakao start");

        final String kakaoUserInfoRequestUrl = kakaoOauthSecretsProvider.getKAKAO_USERINFO_REQUEST_URL();

        try {
            HttpHeaders httpHeaders = setHeaders();
            httpHeaders.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<KakaoOauthUserInfoResponse> kakaoUserInfoResponseForm
                    = restTemplate.postForEntity(kakaoUserInfoRequestUrl, requestEntity, KakaoOauthUserInfoResponse.class);

            log.info("requestUserInfoFromKakao end");
            return kakaoUserInfoResponseForm.getBody();

        } catch (RestClientException e) {
            log.error("Error during requestUserInfoFromKakao: " + e.getMessage());

            Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);
            User user = maybeUser.get();
            KakaoOauthAccessTokenResponse kakaoOauthAccessTokenResponse = refreshKakaoAccessToken(user);

            HttpHeaders httpHeaders = setHeaders();
            httpHeaders.add("Authorization", "Bearer " + kakaoOauthAccessTokenResponse.getAccess_token());

            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<KakaoOauthUserInfoResponse> kakaoUserInfoResponseForm
                    = restTemplate.postForEntity(kakaoUserInfoRequestUrl, requestEntity, KakaoOauthUserInfoResponse.class);

            log.info("requestUserInfoFromKakao end");
            return kakaoUserInfoResponseForm.getBody();
        }
    }

    // 카카오 리프래쉬 토큰으로 액세스 토큰 재발급
    @Override
    public KakaoOauthAccessTokenResponse refreshKakaoAccessToken(User user) {
        log.info("refreshKakaoAccessToken start");

        String refreshToken = user.getRefreshToken();

        // 헤더 설정
        HttpHeaders httpHeaders = setHeaders();

        // 바디 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("client_id", kakaoOauthSecretsProvider.getKAKAO_AUTH_RESTAPI_KEY());
        parameters.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        ResponseEntity<KakaoOauthAccessTokenResponse> kakaoAccessTokenResponseForm = restTemplate.postForEntity(
                kakaoOauthSecretsProvider.getKAKAO_REFRESH_TOKEN_REQUEST_URL(),
                requestEntity,
                KakaoOauthAccessTokenResponse.class);

        final String renewAccessToken = kakaoAccessTokenResponseForm.getBody().getAccess_token();
        final String renewRefreshToken = kakaoAccessTokenResponseForm.getBody().getRefresh_token();

        log.info("new accessToken : " + renewAccessToken);
        log.info("new refreshToken : " + renewRefreshToken);

        user.updateAccessToken(renewAccessToken);

        // refreshToken의 유효 기간이 1개월 미만인 경우 새로운 refreshToken을 받아오므로 새롭게 저장
        if(renewRefreshToken != null) {
            log.info("RefreshToken successfully renewed");
            user.updateRefreshToken(renewRefreshToken);
        }
        userRepository.save(user);

        log.info("Changed accessToken in the database : " + user.getAccessToken());
        log.info("Changed refreshToken in the database : " + user.getRefreshToken());

        log.info("refreshKakaoAccessToken end");
        return kakaoAccessTokenResponseForm.getBody();
    }

    // 카카오 회원 Oauth 연결 끊기
    @Override
    public User disconnectKakaoUser(User user) throws NullPointerException{
        log.info("disconnectKakaoUser start");

        final String kakaoRevokeUrl = kakaoOauthSecretsProvider.getKAKAO_DISCONNECT_REQUEST_URL();

        // 헤더 설정
        HttpHeaders httpHeaders = setHeaders();
        httpHeaders.add("Authorization", "Bearer " + user.getAccessToken());

        // 바디 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("target_id_type", "user_id");
        parameters.add("target_id", user.getId());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        ResponseEntity<KakaoOauthDisconnectUserInfoResponse> kakaoDisconnectUserResponse
                = restTemplate.postForEntity(kakaoRevokeUrl, requestEntity, KakaoOauthDisconnectUserInfoResponse.class);

        try {
            String receivedUserId = kakaoDisconnectUserResponse.getBody().getId().toString();
            Optional<User> foundUser = userRepository.findByStringId(receivedUserId);

            if(foundUser.isEmpty()) {
                log.warn("Can not find user: id - {}", receivedUserId);
                log.info("disconnectKakaoUser end");
                return null;
            }

            User withdrawalUser = foundUser.get();

            log.info("disconnectKakaoUser end");
            return withdrawalUser;

        } catch (Exception e) {
            log.error("Unable to disconnect Kakao user", e);
            log.info("disconnectKakaoUser end");
            return null;
        }
    }
}
