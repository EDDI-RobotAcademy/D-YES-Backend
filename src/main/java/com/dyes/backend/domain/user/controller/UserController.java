package com.dyes.backend.domain.user.controller;

import com.dyes.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    final private UserService userService;
    @GetMapping("/oauth/google/login")
    public RedirectView googleUserLogin (@RequestParam(name = "code") String code) {
        String googleUserTokenWithUrl = userService.googleUserLogin(code);
        RedirectView redirectView = new RedirectView(googleUserTokenWithUrl);
        return redirectView;
    }
    @GetMapping("/oauth/naver/login")
    public RedirectView naverUserLogin (@RequestParam(name = "code") String code) {
        String naverUserTokenWithUrl = userService.naverUserLogin(code);
        RedirectView redirectView = new RedirectView(naverUserTokenWithUrl);
        return redirectView;
    }
}