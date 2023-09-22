package com.dyes.backend.domain.user.controller.form;

import com.dyes.backend.domain.user.entity.AddressBookOption;
import com.dyes.backend.domain.user.service.request.UserAddressOptionChangeRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressOptionChangeRequestForm {
    private String userToken;
    private AddressBookOption addressBookOption;
    private Long addressBookId;
    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }
    public UserAddressOptionChangeRequest toUserAddressOptionChangeRequest() {
        return new UserAddressOptionChangeRequest(addressBookOption, addressBookId);
    }
}
