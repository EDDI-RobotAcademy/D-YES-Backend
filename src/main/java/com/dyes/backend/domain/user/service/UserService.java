package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.controller.form.UserProfileModifyRequestForm;
import com.dyes.backend.domain.user.service.response.UserProfileResponseForm;

public interface UserService {
    String googleUserLogin(String code);
    String naverUserLogin(String code);
    String kakaoUserLogin(String code);
    Boolean checkNickNameDuplicate(String nickName);
    Boolean checkEmailDuplicate(String email);
    UserProfileResponseForm getUserProfile(String userToken);
    UserProfileResponseForm modifyUserProfile(UserProfileModifyRequestForm requestForm);
    boolean userWithdraw(String userToken);
    boolean UserLogOut (String userToken);
}
