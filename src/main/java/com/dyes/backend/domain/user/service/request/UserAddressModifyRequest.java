package com.dyes.backend.domain.user.service.request;

import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressModifyRequest {
    private String address;
    private String zipCode;
    private String addressDetail;

    public Address toAddress() {
        return new Address(address, zipCode, addressDetail);
    }
}
