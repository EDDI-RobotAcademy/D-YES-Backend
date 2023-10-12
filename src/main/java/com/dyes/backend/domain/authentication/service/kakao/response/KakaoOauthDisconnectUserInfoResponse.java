package com.dyes.backend.domain.authentication.service.kakao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoOauthDisconnectUserInfoResponse {
    @JsonProperty("id")
    private Long id;
}