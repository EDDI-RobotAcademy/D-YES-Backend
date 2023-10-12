package com.dyes.backend.domain.authentication.service.naver;

import com.dyes.backend.domain.authentication.service.naver.response.NaverOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;

public interface NaverAuthenticationService {
    NaverUserLoginRequestForm naverLogin(String code);
    NaverOauthUserInfoResponse requestUserInfoFromNaver(String AccessToken);
    String refreshNaverAccessToken(User user);
    User disconnectNaverUser(User user);
}
