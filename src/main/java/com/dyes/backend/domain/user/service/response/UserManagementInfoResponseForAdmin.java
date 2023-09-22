package com.dyes.backend.domain.user.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementInfoResponseForAdmin {
    private LocalDate registrationDate;
    private int registeredUserCount;
}
