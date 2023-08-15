package com.dyes.backend.domain.user.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Embeddable
public class Address {
    private String address;
    private String zipCode;
    private String addressDetail;
}
