package com.dyes.backend.domain.user.service;

import com.dyes.backend.domain.authentication.service.AuthenticationService;
import com.dyes.backend.domain.user.controller.form.*;
import com.dyes.backend.domain.user.entity.*;
import com.dyes.backend.domain.user.repository.AddressBookRepository;
import com.dyes.backend.domain.user.repository.UserProfileRepository;
import com.dyes.backend.domain.user.service.request.UserAddressModifyRequest;
import com.dyes.backend.domain.user.service.request.UserAddressOptionChangeRequest;
import com.dyes.backend.domain.user.service.request.UserAddressUpdateRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import com.dyes.backend.domain.user.service.response.form.UserAddressBookResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserProfileResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dyes.backend.domain.user.entity.AddressBookOption.DEFAULT_OPTION;
import static com.dyes.backend.domain.user.entity.AddressBookOption.NON_DEFAULT_OPTION;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    final private AuthenticationService authenticationService;
    final private UserProfileRepository userProfileRepository;
    final private AddressBookRepository addressBookRepository;

    // 프로필 확인
    @Override
    public UserProfileResponseForm getUserProfile(String userToken) {
        log.info("getUserProfile start");

        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);

        if (maybeUserProfile.isEmpty()) {
            UserProfileResponseForm userProfileResponseForm = new UserProfileResponseForm(user.getId());
            log.info("getUserProfile end");
            return userProfileResponseForm;
        }

        UserProfile userProfile = maybeUserProfile.get();
        UserProfileResponseForm userProfileResponseForm
                = new UserProfileResponseForm(
                user.getId(),
                userProfile.getNickName(),
                userProfile.getEmail(),
                userProfile.getProfileImg(),
                userProfile.getContactNumber(),
                userProfile.getAddress());

        log.info("getUserProfile end");
        return userProfileResponseForm;
    }

    // 프로필 수정
    @Override
    public UserProfileResponseForm modifyUserProfile(UserProfileModifyRequestForm requestForm) {
        log.info("modifyUserProfile start");

        final User user = authenticationService.findUserByUserToken(requestForm.getUserToken());
        if (user == null) {
            return null;
        }

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
        UserProfile userProfile;

        Address address = new Address(requestForm.getAddress(), requestForm.getZipCode(), requestForm.getAddressDetail());
        if (maybeUserProfile.isEmpty()) {

            userProfile = UserProfile.builder()
                    .id(user.getId())
                    .nickName(requestForm.getNickName())
                    .email(requestForm.getEmail())
                    .profileImg(requestForm.getProfileImg())
                    .contactNumber(requestForm.getContactNumber())
                    .address(address)
                    .user(user)
                    .build();
        } else {
            userProfile = maybeUserProfile.get();
            userProfile.updateUserProfile(
                    requestForm.getNickName(), requestForm.getEmail(),
                    requestForm.getProfileImg(), requestForm.getContactNumber(), address);
        }

        userProfileRepository.save(userProfile);

        UserProfileResponseForm userProfileResponseForm
                = new UserProfileResponseForm(
                user.getId(),
                userProfile.getNickName(),
                userProfile.getEmail(),
                userProfile.getProfileImg(),
                userProfile.getContactNumber(),
                userProfile.getAddress());

        log.info("modifyUserProfile end");
        return userProfileResponseForm;
    }

    // 사용자 배송지 정보 업데이트
    @Override
    public Boolean updateAddress(UserAddressModifyRequestForm requestForm) {
        log.info("updateAddress start");

        final User user = authenticationService.findUserByUserToken(requestForm.getUserToken());
        if (user == null) {
            return false;
        }

        UserAddressModifyRequest userAddressModifyRequest = requestForm.toUserAddressModifyRequest();
        Address address = userAddressModifyRequest.toAddress();

        try {
            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
            if (maybeUserProfile.isPresent()) {
                UserProfile userProfile = maybeUserProfile.get();
                userProfile.updateAddress(address);
                userProfileRepository.save(userProfile);
            }
            log.info("updateAddress end");
            return true;

        } catch (Exception e) {
            log.error("Failed to update the user address: {}", e.getMessage(), e);
            return false;
        }
    }

    // 사용자 주소록 조회(배송지 정보)
    @Override
    public List<UserAddressBookResponseForm> getAddressBook(String userToken) {
        log.info("getAddressBook start");

        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        List<UserAddressBookResponseForm> userAddressBookResponseFormList = new ArrayList<>();

        // 주소록 조회
        List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);

        // 주소록이 없을 경우
        if (addressBookList.size() == 0) {
            Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUser(user);
            if (maybeUserProfile.isEmpty()) {
                return null;
            } else {
                UserProfile userProfile = maybeUserProfile.get();
                if (userProfile.getAddress().getAddress() == null || userProfile.getAddress().getAddress().isEmpty()) {
                    log.info("No address is registered.");
                    return null;
                }

                AddressBook addressBook = AddressBook.builder()
                        .addressBookOption(DEFAULT_OPTION)
                        .contactNumber(userProfile.getContactNumber())
                        .address(userProfile.getAddress())
                        .user(user)
                        .build();

                addressBookRepository.save(addressBook);

                UserAddressBookResponseForm userAddressBookResponseForm
                        = new UserAddressBookResponseForm(
                        addressBook.getId(),
                        addressBook.getAddressBookOption(),
                        addressBook.getReceiver(),
                        addressBook.getContactNumber(),
                        addressBook.getAddress());

                userAddressBookResponseFormList.add(userAddressBookResponseForm);

                log.info("getAddressBook end");
                return userAddressBookResponseFormList;
            }
        } else {
            // 주소록이 있을 경우
            for (AddressBook addressBook : addressBookList) {
                UserAddressBookResponseForm userAddressBookResponseForm
                        = new UserAddressBookResponseForm(
                        addressBook.getId(),
                        addressBook.getAddressBookOption(),
                        addressBook.getReceiver(),
                        addressBook.getContactNumber(),
                        addressBook.getAddress());

                userAddressBookResponseFormList.add(userAddressBookResponseForm);
            }

            log.info("getAddressBook end");
            return userAddressBookResponseFormList;
        }
    }

    // 사용자 주소록 추가(배송지 정보)
    @Override
    public Boolean updateAddressBook(UserAddressUpdateRequestForm requestForm) {
        log.info("updateAddressBook start");

        final User user = authenticationService.findUserByUserToken(requestForm.getUserToken());

        if (user == null) {
            return null;
        }

        UserAddressUpdateRequest userAddressUpdateRequest = requestForm.toUserAddressUpdateRequest();

        if (userAddressUpdateRequest.getAddress() == null) {
            log.info("The address to be added to the address book is not available");
            return false;
        }

        List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);

        if (addressBookList.size() == 5) {
            log.info("The maximum registration limit is 5. Unable to add to the address book");
            return false;
        }

        try {
            if (userAddressUpdateRequest.getAddressBookOption().equals(DEFAULT_OPTION)) {
                Optional<AddressBook> maybeAddressBook = addressBookRepository.findByAddressBookOption(DEFAULT_OPTION);
                if (maybeAddressBook.isPresent()) {
                    AddressBook addressBook = maybeAddressBook.get();
                    addressBook.updateAddressBookOption(NON_DEFAULT_OPTION);
                    addressBookRepository.save(addressBook);
                }
            }
            AddressBook addressBook = AddressBook.builder()
                    .addressBookOption(userAddressUpdateRequest.getAddressBookOption())
                    .receiver(userAddressUpdateRequest.getReceiver())
                    .contactNumber(userAddressUpdateRequest.getContactNumber())
                    .address(userAddressUpdateRequest.getAddress())
                    .user(user)
                    .build();

            addressBookRepository.save(addressBook);

            log.info("updateAddressBook end");
            return true;

        } catch (Exception e) {
            log.error("Failed to update the address book: {}", e.getMessage(), e);
            return false;
        }
    }

    // 사용자 주소록 삭제(배송지 정보)
    @Override
    public Boolean deleteAddressBook(Long addressBookId, AddressBookDeleteRequestForm deleteForm) {
        log.info("deleteAddressBook start");

        UserAuthenticationRequest userAuthenticationRequest = deleteForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        // 주소록 삭제 진행
        try {
            AddressBookOption addressBookOption = null;
            List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);
            for (AddressBook addressBook : addressBookList) {
                if (addressBook.getId().equals(addressBookId)) {
                    addressBookOption = addressBook.getAddressBookOption();
                    log.info("This addressBook is DEFAULT_OPTION");
                    addressBookRepository.deleteById(addressBook.getId().toString());
                    log.info("AddressBook deletion successful for addressBook with ID: {}", addressBook.getId());
                }
            }
            if (addressBookOption != null && addressBookOption.equals(DEFAULT_OPTION)) {
                List<AddressBook> remainAddressBookList = addressBookRepository.findAllByUser(user);
                if (remainAddressBookList.size() > 0) {
                    AddressBook remainAddressBook = remainAddressBookList.get(0);
                    remainAddressBook.updateAddressBookOption(DEFAULT_OPTION);
                    addressBookRepository.save(remainAddressBook);
                    log.info("remain address book with ID: {} to be DEFAULT_OPTION: ", remainAddressBook.getId());
                }
            }

            log.info("deleteAddressBook end");
            return true;

        } catch (Exception e) {
            log.error("Failed to delete the addressBook: {}", e.getMessage(), e);
            return false;
        }
    }

    // 사용자 주소록에 있는 배송지 옵션 변경
    @Override
    public Boolean changeAddressBookOption(UserAddressOptionChangeRequestForm requestForm) {
        log.info("changeAddressBookOption start");

        UserAuthenticationRequest userAuthenticationRequest = requestForm.toUserAuthenticationRequest();

        final String userToken = userAuthenticationRequest.getUserToken();
        final User user = authenticationService.findUserByUserToken(userToken);
        if (user == null) {
            return null;
        }

        UserAddressOptionChangeRequest userAddressOptionChangeRequest = requestForm.toUserAddressOptionChangeRequest();
        Long addressBookId = userAddressOptionChangeRequest.getAddressBookId();
        AddressBookOption addressBookOption = userAddressOptionChangeRequest.getAddressBookOption();

        // 주소록에 있는 배송지 옵션 변경 진행
        try {
            List<AddressBook> addressBookList = addressBookRepository.findAllByUser(user);

            for (AddressBook addressBook : addressBookList) {
                if (addressBook.getId().equals(addressBookId)) {
                    if (addressBookOption.equals(DEFAULT_OPTION)) {
                        Optional<AddressBook> maybeAddressBook = addressBookRepository.findByAddressBookOptionAndUser(DEFAULT_OPTION, user);
                        if (maybeAddressBook.isPresent()) {
                            AddressBook defaultOptionAddressBook = maybeAddressBook.get();
                            defaultOptionAddressBook.updateAddressBookOption(NON_DEFAULT_OPTION);
                            addressBookRepository.save(defaultOptionAddressBook);
                        }
                    }
                    addressBook.updateAddressBookOption(addressBookOption);
                    addressBookRepository.save(addressBook);
                    log.info("changeAddressBookOption end");
                    return true;
                }
            }
            return true;

        } catch (Exception e) {
            log.error("Failed to change the addressBook option: {}", e.getMessage(), e);
            return false;
        }
    }
}
