package com.dyes.backend.domain.admin.service;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.admin.service.request.AdminRegisterRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;

public interface AdminService {
    boolean adminRegister(UserAuthenticationRequest userAuthenticationRequest, AdminRegisterRequest adminRegisterRequest);

    Admin findAdminByUserToken(String userToken);
}
