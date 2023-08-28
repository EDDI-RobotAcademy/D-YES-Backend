package com.dyes.backend.domain.authentication.service.naver.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverOauthUserInfoResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("mobile_e164")
    private String mobile_e164;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("age")
    private String age;

    @JsonProperty("birthdate")
    private String birthdate;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("profile_image")
    private String profile_image;

}
