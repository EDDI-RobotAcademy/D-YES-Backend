package com.dyes.backend.domain.authentication.service.kakao;

import com.dyes.backend.domain.authentication.service.kakao.response.KakaoOauthAccessTokenResponse;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;

public interface KakaoAuthenticationService {
    KakaoUserLoginRequestForm kakaoLogin(String code);
    KakaoOauthUserInfoResponse requestUserInfoFromKakao(String accessToken);
    KakaoOauthAccessTokenResponse refreshKakaoAccessToken(User user);
    User disconnectKakaoUser(User user);
}
