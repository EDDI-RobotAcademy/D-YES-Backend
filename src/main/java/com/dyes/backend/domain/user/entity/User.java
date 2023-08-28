package com.dyes.backend.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
}
