package com.dyes.backend.domain.user.controller.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserLoginRequestForm {
    private String accessToken;
    private String refreshToken;
    private String id;
    private String email;
    private String picture;
}
