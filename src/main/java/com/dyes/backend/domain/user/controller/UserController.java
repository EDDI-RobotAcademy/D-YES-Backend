package com.dyes.backend.domain.user.controller;

import com.dyes.backend.domain.user.controller.form.*;
import com.dyes.backend.domain.user.service.UserManagementAdminService;
import com.dyes.backend.domain.user.service.UserManagementService;
import com.dyes.backend.domain.user.service.UserProfileService;
import com.dyes.backend.domain.user.service.response.form.UserAddressBookResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.user.service.response.form.UserProfileResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    final private UserProfileService userProfileService;
    final private UserManagementService userManagementService;
    final private UserManagementAdminService userManagementAdminService;

    // 닉네임 중복 확인
    @GetMapping("/check-nickName")
    public Boolean checkNickNameDuplicate(@RequestParam("nickName") String nickName) {

        return userManagementService.checkNickNameDuplicate(nickName);
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public Boolean checkEmailDuplicate(@RequestParam("email") String email) {

        return userManagementService.checkEmailDuplicate(email);
    }

    // 사용자 프로필 가져오기
    @GetMapping("/userProfile")
    public UserProfileResponseForm getUserProfile(@RequestParam("userToken") String userToken) {

        return userProfileService.getUserProfile(userToken);
    }

    // 사용자 프로필 수정하기
    @PutMapping("/updateInfo")
    public UserProfileResponseForm modifyUserProfile(@RequestBody UserProfileModifyRequestForm requestForm) {

        return userProfileService.modifyUserProfile(requestForm);
    }

    // 사용자 로그아웃
    @GetMapping("/logOut")
    public Boolean userLogOut(@RequestParam("userToken") String userToken) {

        return userManagementService.userLogOut(userToken);
    }

    // 사용자 탈퇴
    @DeleteMapping("/withdrawal")
    public Boolean userWithdrawal(@RequestParam("userToken") String userToken) {

        return userManagementService.userWithdrawal(userToken);
    }

    // 사용자 배송지 정보 업데이트
    @PutMapping("/update-address")
    public Boolean updateAddress(@RequestBody UserAddressModifyRequestForm requestForm) {
        log.info("배송지 업데이트");
        return userProfileService.updateAddress(requestForm);
    }

    // 사용자 주소록 조회(배송지 정보)
    @GetMapping("/address-book")
    public List<UserAddressBookResponseForm> getAddressBook(@RequestParam("userToken") String userToken) {
        return userProfileService.getAddressBook(userToken);
    }

    // 사용자 주소록 추가(배송지 정보)
    @PostMapping("/address-book/update")
    public Boolean updateAddressBook(@RequestBody UserAddressUpdateRequestForm requestForm) {
        return userProfileService.updateAddressBook(requestForm);
    }

    // 사용자 주소록 삭제(배송지 정보)
    @DeleteMapping("/address-book/{addressBookId}")
    public Boolean deleteAddressBook(
            @PathVariable("addressBookId") Long addressBookId,
            @RequestBody AddressBookDeleteRequestForm deleteForm
    ) {
        return userProfileService.deleteAddressBook(addressBookId, deleteForm);
    }

    // 사용자 주소록에 있는 배송지 옵션 변경
    @PutMapping("/address-book/change-option")
    public Boolean changeAddressBookOption(@RequestBody UserAddressOptionChangeRequestForm requestForm) {
        return userProfileService.changeAddressBookOption(requestForm);
    }

    // 관리자의 회원 목록 조회
    @GetMapping("/list")
    public List<UserInfoResponseForm> getUserList(@RequestParam("userToken") String userToken) {

        return userManagementAdminService.getUserList(userToken);
    }

    // 관리자의 신규 회원 목록 조회(7일)
    @GetMapping("/new-list")
    public UserInfoResponseFormForDashBoardForAdmin getNewUserList() {

        return userManagementAdminService.getNewUserList();
    }
}
