package com.dyes.backend.domain.authentication.service.google;

import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.http.ResponseEntity;

public interface GoogleAuthenticationService {
    GoogleUserLoginRequestForm googleUserLogin(String code);
    ResponseEntity<GoogleOauthUserInfoResponse> googleRequestUserInfoWithAccessToken(String accessToken);
    String expiredGoogleAccessTokenRequester(User user);
    User googleUserDisconnect(User user);
}
