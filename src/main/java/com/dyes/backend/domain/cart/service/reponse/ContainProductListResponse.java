package com.dyes.backend.domain.cart.service.reponse;

import com.dyes.backend.domain.product.entity.ProductMainImage;
import com.dyes.backend.domain.product.entity.Unit;
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
    private Long optionId;
    private String productMainImage;
    private Long optionPrice;
    private int optionCount;
    private int optionStock;
    private Long value;
    private Unit unit;
}
