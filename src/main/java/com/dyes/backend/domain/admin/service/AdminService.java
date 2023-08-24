package com.dyes.backend.domain.admin.service;

import com.dyes.backend.domain.admin.controller.form.AdminRegisterRequestForm;

public interface AdminService {
    boolean adminRegister(AdminRegisterRequestForm registerForm);
}
