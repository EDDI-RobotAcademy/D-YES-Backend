package com.dyes.backend.domain.authentication.service;

import com.dyes.backend.domain.user.entity.User;

public interface AuthenticationService {
    User findUserByAccessTokenInDatabase(String accessToken);
    User findUserByUserToken(String userToken);
}
