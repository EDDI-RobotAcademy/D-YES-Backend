package com.dyes.backend.domain.user.service.response.form;

import com.dyes.backend.domain.user.service.response.UserInfoResponseForAdmin;
import com.dyes.backend.domain.user.service.response.UserManagementInfoResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseFormForDashBoardForAdmin {
    private List<UserInfoResponseForAdmin> userInfoResponseForAdminList;
    private List<UserManagementInfoResponseForAdmin> registeredUserCountList;
}
