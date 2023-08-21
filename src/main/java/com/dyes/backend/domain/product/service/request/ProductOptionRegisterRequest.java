package com.dyes.backend.domain.product.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionRegisterRequest {
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private String unit;
}
