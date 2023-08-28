package com.dyes.backend.domain.authentication.service.kakao.service;

import com.dyes.backend.domain.authentication.service.kakao.response.KakaoAccessTokenResponseForm;
import com.dyes.backend.domain.authentication.service.kakao.response.KakaoUserInfoResponseForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;

public interface KakaoAuthenticationService {
    KakaoUserLoginRequestForm kakaoUserLogin(String code);
    KakaoUserInfoResponseForm getUserInfoFromKakao(String accessToken);
    KakaoAccessTokenResponseForm expiredKakaoAccessTokenRequester(User user);
    User kakaoUserDisconnect(User user);
}
