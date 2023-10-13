package com.dyes.backend.domain.authentication.service.google;

import com.dyes.backend.domain.authentication.service.google.response.GoogleOauthUserInfoResponse;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.http.ResponseEntity;

public interface GoogleAuthenticationService {
    GoogleUserLoginRequestForm googleLogin(String code);
    ResponseEntity<GoogleOauthUserInfoResponse> requestUserInfoFromGoogle(String accessToken);
    String refreshGoogleAccessToken(User user);
    User disconnectGoogleUser(User user);
}
