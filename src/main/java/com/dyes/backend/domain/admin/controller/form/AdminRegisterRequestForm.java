package com.dyes.backend.domain.admin.controller.form;

import com.dyes.backend.domain.admin.service.request.AdminRegisterRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequestForm {
    private String userToken;
    private String id;
    private String name;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public AdminRegisterRequest toAdminRegisterRequest() {
        return new AdminRegisterRequest(id, name);
    }
}
