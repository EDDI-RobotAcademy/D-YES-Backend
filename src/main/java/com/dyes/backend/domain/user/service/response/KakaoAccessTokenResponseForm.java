package com.dyes.backend.domain.user.service.response;

import lombok.Getter;

@Getter
public class KakaoAccessTokenResponseForm {
    private String token_type;
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;
}
