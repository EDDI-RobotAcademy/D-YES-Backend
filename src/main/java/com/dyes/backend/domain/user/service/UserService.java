package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.service.response.UserProfileResponseForm;

public interface UserService {
    String googleUserLogin(String code);
    String naverUserLogin(String code);
    String kakaoUserLogin(String code);
    Boolean checkNicknameDuplicate(String nickname);
    Boolean checkEmailDuplicate(String email);
    UserProfileResponseForm getUserProfile(String userToken);
}
