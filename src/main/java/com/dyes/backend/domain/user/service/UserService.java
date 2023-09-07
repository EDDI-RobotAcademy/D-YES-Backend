package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.UserProfileModifyRequestForm;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.service.response.UserInfoResponseForm;
import com.dyes.backend.domain.user.service.response.UserProfileResponseForm;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

public interface UserService {
    Boolean checkNickNameDuplicate(String nickName);
    Boolean checkEmailDuplicate(String email);
    UserProfileResponseForm getUserProfile(String userToken);
    UserProfileResponseForm modifyUserProfile(UserProfileModifyRequestForm requestForm);
    RedirectView userRegisterAndLoginForGoogle(GoogleUserLoginRequestForm requestForm);
    RedirectView userRegisterAndLoginForNaver(NaverUserLoginRequestForm requestForm);
    RedirectView userRegisterAndLoginForKakao(KakaoUserLoginRequestForm requestForm);
    String userLogIn(User user, String platform);
    boolean userLogOut(String userToken);
    boolean userWithdrawal(String userToken);
    List<UserInfoResponseForm> getUserList(String userToken);
}
