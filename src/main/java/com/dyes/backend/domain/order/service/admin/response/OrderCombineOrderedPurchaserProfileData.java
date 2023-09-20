package com.dyes.backend.domain.order.service.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCombineOrderedPurchaserProfileData {
    private String orderedPurchaseName;
    private String orderedPurchaseContactNumber;
    private String orderedPurchaseEmail;
    private String address;
    private String zipCode;
    private String addressDetail;
}
