package com.dyes.backend.domain.farm.controller.form;

import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmDeleteRequestForm {
    private String userToken;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }
}
