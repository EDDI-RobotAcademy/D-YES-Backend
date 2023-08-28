package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.UserProfileModifyRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.service.response.UserProfileResponseForm;
import org.springframework.web.servlet.view.RedirectView;

public interface UserService {
//    String kakaoUserLogin(String code);
    Boolean checkNickNameDuplicate(String nickName);
    Boolean checkEmailDuplicate(String email);
    UserProfileResponseForm getUserProfile(String userToken);
    UserProfileResponseForm modifyUserProfile(UserProfileModifyRequestForm requestForm);
    RedirectView userRegisterAndLoginForGoogle(GoogleUserLoginRequestForm requestForm);
    RedirectView userRegisterAndLoginForNaver(NaverUserLoginRequestForm requestForm);
    String userLogIn(User user, String platform);
    boolean userLogOut(String userToken);
    boolean userWithdrawal(String userToken);
}
