package com.dyes.backend.domain.authentication.controller;

import com.dyes.backend.domain.authentication.service.google.GoogleAuthenticationService;
import com.dyes.backend.domain.user.controller.form.GoogleUserLoginRequestForm;
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
    final private UserService userService;

    // 구글 로그인 및 회원가입
    @GetMapping("/google/login")
    public RedirectView googleUserLogin(@RequestParam(name = "code") String code) {
        GoogleUserLoginRequestForm requestForm = googleAuthenticationService.googleUserLogin(code);
        return userService.userRegisterAndLoginForGoogle(requestForm);
    }
}
