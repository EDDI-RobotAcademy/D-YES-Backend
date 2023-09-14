package com.dyes.backend.domain.user.service.response.form;

import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseForm {
    private String userId;
    private String nickName;
    private String email;
    private String profileImg;
    private String contactNumber;
    private Address address;

    public UserProfileResponseForm(String userId) {
        this.userId = userId;
    }
}
