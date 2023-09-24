package com.dyes.backend.domain.user.service.response;

import com.dyes.backend.domain.user.entity.Active;
import com.dyes.backend.domain.user.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseForAdmin {
    private String userId;
    private UserType userType;
    private Active active;
    private LocalDate registrationDate;
}
