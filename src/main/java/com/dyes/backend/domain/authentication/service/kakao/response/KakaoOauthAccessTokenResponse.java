package com.dyes.backend.domain.authentication.service.kakao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoOauthAccessTokenResponse {
    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private int expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("refresh_token_expires_in")
    private int refresh_token_expires_in;
}
