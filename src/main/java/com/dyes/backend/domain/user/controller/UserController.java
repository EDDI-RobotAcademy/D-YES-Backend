package com.dyes.backend.domain.user.controller;

import com.dyes.backend.domain.user.service.UserService;
import com.dyes.backend.domain.user.service.response.UserProfileResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    final private UserService userService;

    // 구글 로그인 및 회원가입
    @GetMapping("/oauth/google/login")
    public RedirectView googleUserLogin (@RequestParam(name = "code") String code) {
        String googleUserTokenWithUrl = userService.googleUserLogin(code);
        RedirectView redirectView = new RedirectView(googleUserTokenWithUrl);
        return redirectView;
    }

    // 네이버 로그인 및 회원가입
    @GetMapping("/oauth/naver/login")
    public RedirectView naverUserLogin (@RequestParam(name = "code") String code) {
        String naverUserTokenWithUrl = userService.naverUserLogin(code);
        RedirectView redirectView = new RedirectView(naverUserTokenWithUrl);
        return redirectView;
    }

    // 카카오 로그인 및 회원가입
    @GetMapping("/oauth/kakao/login")
    public RedirectView kakaoUserLogin(@RequestParam(name = "code") String code) {
        String kakaoUserTokenWithUrl = userService.kakaoUserLogin(code);
        RedirectView redirectView = new RedirectView(kakaoUserTokenWithUrl);
        return redirectView;
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public Boolean checkNicknameDuplicate(@RequestParam("nickname") String nickname) {

        return userService.checkNicknameDuplicate(nickname);
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
}
