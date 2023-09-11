package com.dyes.backend.domain.order.service.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedPurchaserProfileRequest {
    private String orderedPurchaserName;
    private String orderedPurchaserContactNumber;
    private String orderedPurchaserEmail;
    private String orderedPurchaserAddress;
    private String orderedPurchaserZipCode;
    private String orderedPurchaserAddressDetail;
}
