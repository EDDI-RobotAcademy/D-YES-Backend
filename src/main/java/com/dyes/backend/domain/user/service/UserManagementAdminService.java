package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.service.response.form.UserInfoResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseFormForDashBoardForAdmin;

import java.util.List;

public interface UserManagementAdminService {
    List<UserInfoResponseForm> getUserList(String userToken);
    UserInfoResponseFormForDashBoardForAdmin getNewUserList();
}
