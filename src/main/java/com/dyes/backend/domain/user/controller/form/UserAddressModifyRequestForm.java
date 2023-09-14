package com.dyes.backend.domain.user.controller.form;

import com.dyes.backend.domain.user.service.request.UserAddressModifyRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressModifyRequestForm {
    private String userToken;
    private String address;
    private String zipCode;
    private String addressDetail;

    public UserAddressModifyRequest toUserAddressModifyRequest() {
        return new UserAddressModifyRequest(address, zipCode, addressDetail);
    }
}
