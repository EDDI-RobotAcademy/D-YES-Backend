package com.dyes.backend.domain.admin.controller;

import com.dyes.backend.domain.admin.controller.form.AdminRegisterRequestForm;
import com.dyes.backend.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    final private AdminService adminService;

    // 일반 관리자 등록
    @PostMapping("/register")
    public boolean adminRegister(@RequestBody AdminRegisterRequestForm registerForm) {

        return adminService.adminRegister(registerForm.toUserAuthenticationRequest(), registerForm.toAdminRegisterRequest());
    }

}
