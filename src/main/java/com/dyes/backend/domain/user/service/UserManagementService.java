package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.controller.form.*;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.web.servlet.view.RedirectView;

public interface UserManagementService {
    Boolean checkNickNameDuplicate(String nickName);
    Boolean checkEmailDuplicate(String email);
    RedirectView userRegisterAndLoginForGoogle(GoogleUserLoginRequestForm requestForm);
    RedirectView userRegisterAndLoginForNaver(NaverUserLoginRequestForm requestForm);
    RedirectView userRegisterAndLoginForKakao(KakaoUserLoginRequestForm requestForm);
    String userLogIn(User user, String platform);
    boolean userLogOut(String userToken);
    boolean userWithdrawal(String userToken);
}
