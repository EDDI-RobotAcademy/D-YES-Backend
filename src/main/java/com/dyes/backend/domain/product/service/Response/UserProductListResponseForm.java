package com.dyes.backend.domain.product.service.Response;

import com.dyes.backend.domain.product.entity.CultivationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProductListResponseForm {
    private Long productId;
    private String productName;
    private CultivationMethod cultivationMethod;
    private String productMainImage;
    private Long minOptionPrice;
    private Boolean isSoldOut;
}