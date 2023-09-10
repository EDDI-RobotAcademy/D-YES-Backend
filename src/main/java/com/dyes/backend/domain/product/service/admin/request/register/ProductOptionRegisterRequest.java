package com.dyes.backend.domain.product.service.admin.request.register;

import com.dyes.backend.domain.product.entity.Unit;
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
    private Unit unit;
}
