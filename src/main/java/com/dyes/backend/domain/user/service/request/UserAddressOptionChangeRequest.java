package com.dyes.backend.domain.user.service.request;

import com.dyes.backend.domain.user.entity.AddressBookOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressOptionChangeRequest {
    private AddressBookOption addressBookOption;
    private Long addressBookId;
}
