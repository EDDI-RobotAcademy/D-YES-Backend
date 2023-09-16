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
    private int tax_free;
    private int tax;
    private int vat;
    private int point;
    private int discount;
    private int green_deposit;
}
