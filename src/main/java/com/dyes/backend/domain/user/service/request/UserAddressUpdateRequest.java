package com.dyes.backend.domain.user.service.request;

import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.entity.AddressBookOption;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressUpdateRequest {
    private AddressBookOption addressBookOption;
    private String receiver;
    private String contactNumber;
    @Embedded
    private Address address;
}
