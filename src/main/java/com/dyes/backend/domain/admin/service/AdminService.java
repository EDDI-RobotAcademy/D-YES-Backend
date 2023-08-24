package com.dyes.backend.domain.admin.service;

import com.dyes.backend.domain.admin.controller.form.AdminRegisterRequestForm;
import com.dyes.backend.domain.admin.entity.Admin;

public interface AdminService {
    boolean adminRegister(AdminRegisterRequestForm registerForm);
    Admin findAdminByUserToken(String userToken);
}
