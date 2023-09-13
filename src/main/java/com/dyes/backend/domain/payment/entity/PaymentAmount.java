package com.dyes.backend.domain.payment.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAmount {
    private int total;
    private double tax_free;
    private double tax;
    private int point;
    private int discount;
}
