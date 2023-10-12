package com.dyes.backend.domain.authentication.controller;

import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.kakao.KakaoAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class AuthenticationController {
    final private GoogleAuthenticationService googleAuthenticationService;
    final private NaverAuthenticationService naverAuthenticationService;
    final private KakaoAuthenticationService kakaoAuthenticationService;
    final private UserManagementService userManagementService;

    // 구글 로그인 및 회원가입
    @GetMapping("/google/login")
    public RedirectView googleUserLogin(@RequestParam(name = "code") String code) {
        GoogleUserLoginRequestForm requestForm = googleAuthenticationService.googleLogin(code);
        return userManagementService.userRegisterAndLoginForGoogle(requestForm);
    }

    // 네이버 로그인 및 회원가입
    @GetMapping("/naver/login")
    public RedirectView naverUserLogin(@RequestParam(name = "code") String code) {
        NaverUserLoginRequestForm requestForm = naverAuthenticationService.naverLogin(code);
        return userManagementService.userRegisterAndLoginForNaver(requestForm);
    }

    // 카카오 로그인 및 회원가입
    @GetMapping("/kakao/login")
    public RedirectView kakaoUserLogin(@RequestParam(name = "code") String code) {
        KakaoUserLoginRequestForm requestForm = kakaoAuthenticationService.kakaoLogin(code);
        return userManagementService.userRegisterAndLoginForKakao(requestForm);
    }
}
