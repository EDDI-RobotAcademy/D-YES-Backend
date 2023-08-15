package com.dyes.backend.domain.user.controller.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileModifyRequestForm {
    private String userToken;
    private String nickName;
    private String email;
    private String profileImg;
    private String contactNumber;
    private String address;
    private String zipCode;
    private String addressDetail;
}
