package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.user.controller.form.*;
import com.dyes.backend.domain.user.service.response.form.UserAddressBookResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserProfileResponseForm;

import java.util.List;

public interface UserProfileService {
    UserProfileResponseForm getUserProfile(String userToken);
    UserProfileResponseForm modifyUserProfile(UserProfileModifyRequestForm requestForm);
    Boolean updateAddress(UserAddressModifyRequestForm requestForm);
    List<UserAddressBookResponseForm> getAddressBook(String userToken);
    Boolean updateAddressBook(UserAddressUpdateRequestForm requestForm);
    Boolean deleteAddressBook(Long addressBookId, AddressBookDeleteRequestForm deleteForm);
    Boolean changeAddressBookOption(UserAddressOptionChangeRequestForm requestForm);
}
