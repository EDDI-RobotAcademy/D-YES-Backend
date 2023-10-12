package com.dyes.backend.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String accessToken;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Active active;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateAllTokenWithActive(String accessToken, String refreshToken, Active active) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.active = active;
    }

    public void updateActive(Active active) {
        this.active = active;
    }
}
