package com.dyes.backend.domain.user.service.response.form;

import com.dyes.backend.domain.admin.entity.RoleType;
import com.dyes.backend.domain.user.entity.Active;
import com.dyes.backend.domain.user.entity.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponseForm {
    private String userId;
    private UserType userType;
    private Active active;
    private RoleType roleType;

    public UserInfoResponseForm(String userId, UserType userType, Active active, RoleType roleType) {
        this.userId = userId;
        this.userType = userType;
        this.active = active;
        this.roleType = roleType;
    }

    public UserInfoResponseForm(String userId, UserType userType, Active active) {
        this.userId = userId;
        this.userType = userType;
        this.active = active;
    }
}
