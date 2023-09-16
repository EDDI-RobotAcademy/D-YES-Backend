package com.dyes.backend.domain.user.controller;

import com.dyes.backend.domain.user.controller.form.UserAddressModifyRequestForm;
import com.dyes.backend.domain.user.controller.form.UserAddressUpdateRequestForm;
import com.dyes.backend.domain.user.controller.form.UserProfileModifyRequestForm;
import com.dyes.backend.domain.user.service.UserService;
import com.dyes.backend.domain.user.service.response.form.UserAddressBookResponseForm;
import com.dyes.backend.domain.user.service.response.form.UserInfoResponseForm;
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
    final private UserService userService;

    // 닉네임 중복 확인
    @GetMapping("/check-nickName")
    public Boolean checkNickNameDuplicate(@RequestParam("nickName") String nickName) {

        return userService.checkNickNameDuplicate(nickName);
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public Boolean checkEmailDuplicate(@RequestParam("email") String email) {

        return userService.checkEmailDuplicate(email);
    }

    // 사용자 프로필 가져오기
    @GetMapping("/userProfile")
    public UserProfileResponseForm getUserProfile(@RequestParam("userToken") String userToken) {

        return userService.getUserProfile(userToken);
    }

    // 사용자 프로필 수정하기
    @PutMapping("/updateInfo")
    public UserProfileResponseForm modifyUserProfile(@RequestBody UserProfileModifyRequestForm requestForm) {

        return userService.modifyUserProfile(requestForm);
    }

    // 사용자 로그아웃
    @GetMapping("/logOut")
    public Boolean userLogOut(@RequestParam("userToken") String userToken) {

        return userService.userLogOut(userToken);
    }

    // 사용자 탈퇴
    @DeleteMapping("/withdrawal")
    public Boolean userWithdrawal(@RequestParam("userToken") String userToken) {

        return userService.userWithdrawal(userToken);
    }

    // 사용자 배송지 정보 업데이트
    @PutMapping("/update-address")
    public Boolean updateAddress(@RequestBody UserAddressModifyRequestForm requestForm) {
        log.info("배송지 업데이트");
        return userService.updateAddress(requestForm);
    }

    // 사용자 주소록 조회(배송지 정보)
    @GetMapping("/address-book")
    public List<UserAddressBookResponseForm> getAddressBook(@RequestParam("userToken") String userToken) {
        return userService.getAddressBook(userToken);
    }

    // 사용자 주소록 추가(배송지 정보)
    @PostMapping("/address-book/update")
    public Boolean updateAddressBook(@RequestBody UserAddressUpdateRequestForm requestForm) {
        return userService.updateAddressBook(requestForm);
    }

    // 관리자의 회원 목록 조회
    @GetMapping("/list")
    public List<UserInfoResponseForm> getUserList(@RequestParam("userToken") String userToken) {

        return userService.getUserList(userToken);
    }
}
