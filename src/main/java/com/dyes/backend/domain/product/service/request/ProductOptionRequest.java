package com.dyes.backend.domain.product.service.request;

import com.dyes.backend.domain.product.entity.Amount;
import com.dyes.backend.domain.product.entity.CultivationMethod;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionRequest {
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private String unit;
}
