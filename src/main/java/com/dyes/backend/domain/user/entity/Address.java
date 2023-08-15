package com.dyes.backend.domain.user.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String address;
    private String zipCode;
    private String addressDetail;
}
