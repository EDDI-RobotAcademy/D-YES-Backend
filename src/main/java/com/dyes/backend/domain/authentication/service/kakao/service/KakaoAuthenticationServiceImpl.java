package com.dyes.backend.domain.authentication.service.kakao.service;

import com.dyes.backend.domain.authentication.service.kakao.response.KakaoAccessTokenResponseForm;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoDisconnectUserIdResponseForm;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoUserInfoResponseForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.provider.KakaoOauthSecretsProvider;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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
@ToString
@RequiredArgsConstructor
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {
    final private KakaoOauthSecretsProvider kakaoOauthSecretsProvider;
    final private UserRepository userRepository;
    final private RestTemplate restTemplate;

    // 카카오 로그인
    @Override
    public KakaoUserLoginRequestForm kakaoUserLogin(String code) {
        // 카카오 서버에서 accessToken 받아오기
        KakaoAccessTokenResponseForm kakaoAccessTokenResponseForm = getAccessTokenFromKakao(code);
        final String accessToken = kakaoAccessTokenResponseForm.getAccess_token();
        final String refreshToken = kakaoAccessTokenResponseForm.getRefresh_token();

        log.info("kakao accessToken: " + accessToken);
        log.info("kakao refreshToken: " + refreshToken);

        // 카카오 서버에서 받아온 accessToken으로 사용자 정보 받아오기
        KakaoUserInfoResponseForm kakaoUserInfoResponseForm = getUserInfoFromKakao(accessToken);

        KakaoUserLoginRequestForm kakaoUserLoginRequestForm
                = new KakaoUserLoginRequestForm(
                        kakaoAccessTokenResponseForm.getAccess_token(),
                        kakaoAccessTokenResponseForm.getRefresh_token(),
                        kakaoUserInfoResponseForm.getId(),
                        kakaoUserInfoResponseForm.getProperties().getNickname(),
                        kakaoUserInfoResponseForm.getProperties().getProfile_image());

        return kakaoUserLoginRequestForm;
    }

    // 카카오에서 인가 코드를 받으면 엑세스 토큰 요청
    public KakaoAccessTokenResponseForm getAccessTokenFromKakao(String code) {
        // 헤더 설정
        HttpHeaders httpHeaders = setHeaders();

        // 바디 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", kakaoOauthSecretsProvider.getKAKAO_AUTH_RESTAPI_KEY());
        parameters.add("redirect_uri", kakaoOauthSecretsProvider.getKAKAO_AUTH_REDIRECT_URL());
        parameters.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        ResponseEntity<KakaoAccessTokenResponseForm> kakaoAccessTokenResponseForm = restTemplate.postForEntity(
                kakaoOauthSecretsProvider.getKAKAO_TOKEN_REQUEST_URL(),
                requestEntity,
                KakaoAccessTokenResponseForm.class);

        return kakaoAccessTokenResponseForm.getBody();
    }

    // 카카오 엑세스 토큰으로 유저 정보 요청
    @Override
    public KakaoUserInfoResponseForm getUserInfoFromKakao(String accessToken) {

        try {
            // 헤더 설정
            HttpHeaders httpHeaders = setHeaders();
            httpHeaders.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<KakaoUserInfoResponseForm> kakaoUserInfoResponseForm = restTemplate.postForEntity(
                    kakaoOauthSecretsProvider.getKAKAO_USERINFO_REQUEST_URL(),
                    requestEntity,
                    KakaoUserInfoResponseForm.class);

            return kakaoUserInfoResponseForm.getBody();

        } catch (RestClientException e) {
            log.error("Error during requestUserInfoWithAccessTokenForSignIn: " + e.getMessage());
            Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);
            User user = maybeUser.get();
            KakaoAccessTokenResponseForm kakaoAccessTokenResponseForm = expiredKakaoAccessTokenRequester(user);

            // 헤더 설정
            HttpHeaders httpHeaders = setHeaders();
            httpHeaders.add("Authorization", "Bearer " + kakaoAccessTokenResponseForm.getAccess_token());

            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<KakaoUserInfoResponseForm> kakaoUserInfoResponseForm = restTemplate.postForEntity(
                    kakaoOauthSecretsProvider.getKAKAO_USERINFO_REQUEST_URL(),
                    requestEntity,
                    KakaoUserInfoResponseForm.class);

            return kakaoUserInfoResponseForm.getBody();
        }
    }

    // 카카오 리프래쉬 토큰으로 엑세스 토큰 재발급
    @Override
    public KakaoAccessTokenResponseForm expiredKakaoAccessTokenRequester(User user) {

        String refreshToken = user.getRefreshToken();

        // 헤더 설정
        HttpHeaders httpHeaders = setHeaders();

        // 바디 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("client_id", kakaoOauthSecretsProvider.getKAKAO_AUTH_RESTAPI_KEY());
        parameters.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        ResponseEntity<KakaoAccessTokenResponseForm> kakaoAccessTokenResponseForm = restTemplate.postForEntity(
                kakaoOauthSecretsProvider.getKAKAO_REFRESH_TOKEN_REQUEST_URL(),
                requestEntity,
                KakaoAccessTokenResponseForm.class);

        final String renewAccessToken = kakaoAccessTokenResponseForm.getBody().getAccess_token();
        final String renewRefreshToken = kakaoAccessTokenResponseForm.getBody().getRefresh_token();

        user.setAccessToken(renewAccessToken);

        // refreshToken의 유효 기간이 1개월 미만인 경우 새로운 refreshToken을 받아오므로 새롭게 저장
        if(renewAccessToken.equals(null)) {
            log.info("RefreshToken successfully renewed");
            user.setRefreshToken(renewRefreshToken);
        }
        userRepository.save(user);

        return kakaoAccessTokenResponseForm.getBody();
    }

    // 카카오 회원 Oauth 연결 끊기
    @Override
    public User kakaoUserDisconnect(User user) throws NullPointerException{

        // 헤더 설정
        HttpHeaders httpHeaders = setHeaders();
        httpHeaders.add("Authorization", "Bearer " + user.getAccessToken());

        // 바디 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("target_id_type", "user_id");
        parameters.add("target_id", user.getId());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, httpHeaders);

        ResponseEntity<KakaoDisconnectUserIdResponseForm> kakaoDisconnectUserResponse = restTemplate.postForEntity(
                kakaoOauthSecretsProvider.getKAKAO_DISCONNECT_REQUEST_URL(),
                requestEntity,
                KakaoDisconnectUserIdResponseForm.class);

        try {
            String receivedUserId = kakaoDisconnectUserResponse.getBody().getId().toString();
            Optional<User> foundUser = userRepository.findByStringId(receivedUserId);
            if(foundUser.isEmpty()) {
                log.info("Cannot find User");
                return null;
            }

            User withdrawalUser = foundUser.get();
            return withdrawalUser;

        } catch (RestClientException e) {
            log.error("Error during kakaoUserWithdrawal: " + e.getMessage());

            return null;
        }
    }
}
