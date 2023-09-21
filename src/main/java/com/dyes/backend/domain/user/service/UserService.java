package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.controller.form.*;
import com.dyes.backend.domain.user.entity.User;
import com.dyes.backend.domain.user.service.response.form.UserAddressBookResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserProfileResponseForm;
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
    Boolean updateAddress(UserAddressModifyRequestForm requestForm);
    List<UserAddressBookResponseForm> getAddressBook(String userToken);
    Boolean updateAddressBook(UserAddressUpdateRequestForm requestForm);
    List<UserInfoResponseForm> getUserList(String userToken);
    Boolean deleteAddressBook(Long addressBookId, AddressBookDeleteRequestForm deleteForm);
    Boolean changeAddressBookOption(UserAddressOptionChangeRequestForm requestForm);
}
