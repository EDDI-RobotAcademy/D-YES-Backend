package com.dyes.backend.domain.product.service.user.response;

import com.dyes.backend.domain.product.entity.CultivationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseForListForUser {
    private Long productId;
    private String productName;
    private CultivationMethod cultivationMethod;
}
