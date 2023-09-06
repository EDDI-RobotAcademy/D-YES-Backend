package com.dyes.backend.domain.order.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class OrderedPurchaserProfileAddress {
    private String orderedPurchaseAddress;
    private String orderedPurchaseZipCode;
    private String orderedPurchaseAddressDetail;
}
