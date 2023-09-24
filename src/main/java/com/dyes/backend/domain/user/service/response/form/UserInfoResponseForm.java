package com.dyes.backend.domain.user.service.response.form;

import com.dyes.backend.domain.admin.entity.RoleType;
import com.dyes.backend.domain.user.entity.Active;
import com.dyes.backend.domain.user.entity.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserInfoResponseForm {
    private String userId;
    private UserType userType;
    private Active active;
    private RoleType roleType;
    private LocalDate registeredDate;

    public UserInfoResponseForm(String userId, UserType userType, Active active, RoleType roleType, LocalDate registeredDate) {
        this.userId = userId;
        this.userType = userType;
        this.active = active;
        this.roleType = roleType;
        this.registeredDate = registeredDate;
    }

    public UserInfoResponseForm(String userId, UserType userType, Active active, LocalDate registeredDate) {
        this.userId = userId;
        this.userType = userType;
        this.active = active;
        this.registeredDate = registeredDate;
    }
}
