package com.dyes.backend.domain.authentication.service;

import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.response.NaverOauthUserInfoResponse;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.entity.UserType;
import com.dyes.backend.domain.user.repository.UserRepository;
import com.dyes.backend.utility.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
    final private GoogleAuthenticationService googleAuthenticationService;
    final private NaverAuthenticationService naverAuthenticationService;
    final private UserRepository userRepository;
    final private RedisService redisService;

    // accessToken으로 DB에서 사용자 조회
    @Override
    public User findUserByAccessTokenInDatabase(String accessToken) {
        log.info("findUserByAccessTokenInDatabase Start");

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
        final String accessToken = redisService.getAccessToken(userToken);
        if(accessToken == null){
            log.info("AccessToken is empty");
            return null;
        }

        final User user = findUserByAccessTokenInDatabase(accessToken);
        if(user == null){
            return null;
        }

        final UserType userType = user.getUserType();

        String userId = "";
        if(userType.equals(UserType.GOOGLE)) {
            ResponseEntity<GoogleOauthUserInfoResponse> googleOauthUserInfoResponse
                    = googleAuthenticationService.googleRequestUserInfoWithAccessToken(accessToken);
            userId = googleOauthUserInfoResponse.getBody().getId();
        } else if(userType.equals(UserType.NAVER)) {
            NaverOauthUserInfoResponse naverOauthUserInfoResponse
                    = naverAuthenticationService.naverRequestUserInfoWithAccessToken(accessToken);
            userId = naverOauthUserInfoResponse.getId();
        } else if(userType.equals(UserType.KAKAO)) {
//            KakaoUserInfoResponseForm kakaoUserInfoResponseForm
//                    = kakaoAuthenticationService.getUserInfoFromKakao(accessToken);
//            userId = kakaoUserInfoResponseForm.getId();
        }

        if(userId.equals(user.getId())) {
            return user;
        }

        return null;
    }
}
