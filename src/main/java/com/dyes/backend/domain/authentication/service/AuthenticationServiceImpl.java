package com.dyes.backend.domain.authentication.service;

import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoOauthUserInfoResponse;
import com.dyes.backend.domain.authentication.service.kakao.KakaoAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.response.NaverOauthUserInfoResponse;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserType;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    final private GoogleAuthenticationService googleAuthenticationService;
    final private NaverAuthenticationService naverAuthenticationService;
    final private KakaoAuthenticationService kakaoAuthenticationService;
    final private UserRepository userRepository;
    final private RedisService redisService;

    // accessToken으로 DB에서 사용자 조회
    @Override
    public User findUserByAccessTokenInDatabase(String accessToken) {
        log.info("findUserByAccessTokenInDatabase start");

        Optional<User> maybeUser = userRepository.findByAccessToken(accessToken);

        if (maybeUser.isEmpty()) {
            log.warn("Can not find user: access token - {}", accessToken);
            log.info("findUserByAccessTokenInDatabase end");
            return null;
        }

        User user = maybeUser.get();

        log.info("findUserByAccessTokenInDatabase end");
        return user;
    }

    // userToken으로 Redis에서 accessToken 조회 후 Oauth 서버로 사용자 정보 요청
    @Override
    public User findUserByUserToken(String userToken) {
        log.info("findUserByUserToken start");

        final String accessToken = redisService.getAccessToken(userToken);

        if (accessToken == null) {
            log.info("AccessToken does not exist in Redis");
            return null;
        }

        final User user = findUserByAccessTokenInDatabase(accessToken);

        if (user == null) {
            return null;
        }

        final UserType userType = user.getUserType();
        String userId = getUserIdFromOauth(accessToken, userType);

        if (userId.equals(user.getId())) {
            Optional<User> maybeUser = userRepository.findById(userId);
            log.info("Original user's accessToken: " + accessToken);
            log.info("Updated user's accessToken: " + maybeUser.get().getAccessToken());

            User foundUser = new User();
            if (maybeUser.isPresent()) {
                foundUser = maybeUser.get();

                if (!foundUser.getAccessToken().equals(accessToken)) {
                    log.info("AccessToken is refreshed");
                    redisService.deleteKeyAndValueWithUserToken(userToken);
                    redisService.setUserTokenAndUser(userToken, foundUser.getAccessToken());
                }
            }
            log.info("findUserByUserToken end");
            return foundUser;
        }

        return null;
    }

    // accessToken으로 Oauth 서버로 사용자 정보(id) 요청
    private String getUserIdFromOauth(String accessToken, UserType userType) {
        log.info("getUserIdFromOauth start");

        String userId = "";
        switch (userType) {
            case GOOGLE:
                ResponseEntity<GoogleOauthUserInfoResponse> googleResponse =
                        googleAuthenticationService.requestUserInfoFromGoogle(accessToken);
                userId = googleResponse.getBody().getId();
                break;
            case NAVER:
                NaverOauthUserInfoResponse naverResponse =
                        naverAuthenticationService.requestUserInfoFromNaver(accessToken);
                userId = naverResponse.getId();
                break;
            case KAKAO:
                KakaoOauthUserInfoResponse kakaoResponse =
                        kakaoAuthenticationService.requestUserInfoFromKakao(accessToken);
                userId = kakaoResponse.getId();
                break;
            default:
                log.error("Invalid user type");
                break;
        }

        log.info("getUserIdFromOauth end");
        return userId;
    }
}
