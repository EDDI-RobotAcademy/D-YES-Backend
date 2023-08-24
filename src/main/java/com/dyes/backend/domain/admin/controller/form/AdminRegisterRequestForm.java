package com.dyes.backend.domain.admin.controller.form;

import com.dyes.backend.domain.admin.service.request.AdminRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequestForm {
    private String userToken;
    private String id;
    private String name;

    public AdminRegisterRequest toAdminRegisterRequest () {
        return new AdminRegisterRequest(id, name);
    }
}
