package com.dyes.backend.domain.user.service.response.form;

import com.dyes.backend.domain.user.entity.Address;
import com.dyes.backend.domain.user.entity.AddressBookOption;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressBookResponseForm {
    private Long addressId;
    private AddressBookOption addressBookOption;
    private String receiver;
    private String contactNumber;
    @Embedded
    private Address address;
}
