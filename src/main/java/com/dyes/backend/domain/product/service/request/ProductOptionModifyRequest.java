package com.dyes.backend.domain.product.service.request;

import com.dyes.backend.domain.product.entity.SaleStatus;
import com.dyes.backend.domain.product.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionModifyRequest {
    private Long optionId;
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private Unit unit;
    private SaleStatus optionSaleStatus;
}
