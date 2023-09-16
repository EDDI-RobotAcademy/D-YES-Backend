package com.dyes.backend.domain.user.controller.form;

import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.entity.AddressBookOption;
import com.dyes.backend.domain.user.service.request.UserAddressUpdateRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressUpdateRequestForm {
    private String userToken;
    private AddressBookOption addressBookOption;
    private String receiver;
    private String contactNumber;
    @Embedded
    private Address address;
    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }
    public UserAddressUpdateRequest toUserAddressUpdateRequest() {
        return new UserAddressUpdateRequest(addressBookOption, receiver, contactNumber, address);
    }
}
