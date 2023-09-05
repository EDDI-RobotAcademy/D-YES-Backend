package com.dyes.backend.domain.order.service.response;

import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmProductResponse {
    private Long optionId;
    private String productMainImage;
    private String productName;
    private Long value;
    private Unit unit;
    private int optionCount;
    private Long optionPrice;
}
