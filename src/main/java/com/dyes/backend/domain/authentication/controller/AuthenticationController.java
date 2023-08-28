package com.dyes.backend.domain.authentication.controller;

import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.authentication.service.kakao.service.KakaoAuthenticationService;
import com.dyes.backend.domain.authentication.service.naver.NaverAuthenticationService;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.KakaoUserLoginRequestForm;
import com.dyes.backend.domain.user.controller.form.NaverUserLoginRequestForm;
import com.dyes.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class AuthenticationController {
    final private GoogleAuthenticationService googleAuthenticationService;
    final private NaverAuthenticationService naverAuthenticationService;
    final private KakaoAuthenticationService kakaoAuthenticationService;
    final private UserService userService;

    // 구글 로그인 및 회원가입
    @GetMapping("/google/login")
    public RedirectView googleUserLogin(@RequestParam(name = "code") String code) {
        GoogleUserLoginRequestForm requestForm = googleAuthenticationService.googleUserLogin(code);
        return userService.userRegisterAndLoginForGoogle(requestForm);
    }

    // 네이버 로그인 및 회원가입
    @GetMapping("/naver/login")
    public RedirectView naverUserLogin(@RequestParam(name = "code") String code) {
        NaverUserLoginRequestForm requestForm = naverAuthenticationService.naverUserLogin(code);
        return userService.userRegisterAndLoginForNaver(requestForm);
    }

    // 카카오 로그인 및 회원가입
    @GetMapping("/kakao/login")
    public RedirectView kakaoUserLogin(@RequestParam(name = "code") String code) {
        KakaoUserLoginRequestForm requestForm = kakaoAuthenticationService.kakaoUserLogin(code);
        return userService.userRegisterAndLoginForKakao(requestForm);
    }
}
