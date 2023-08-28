package com.dyes.backend.domain.authentication.service.naver;

import com.dyes.backend.domain.authentication.service.naver.response.NaverOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;

public interface NaverAuthenticationService {
    NaverUserLoginRequestForm naverUserLogin(String code);
    NaverOauthUserInfoResponse naverRequestUserInfoWithAccessToken(String AccessToken);
    String expiredNaverAccessTokenRequester(User user);
    User naverUserDisconnect(User user);
}
