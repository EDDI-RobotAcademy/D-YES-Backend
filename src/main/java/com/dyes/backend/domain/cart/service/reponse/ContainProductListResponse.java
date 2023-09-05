package com.dyes.backend.domain.cart.service.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainProductListResponse {
    private String productName;
    private String productMainImage;
    private Long optionId;
    private String optionName;
    private Long optionPrice;
    private int optionCount;
}
