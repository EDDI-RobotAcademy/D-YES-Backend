package com.dyes.backend.domain.product.service.user.response.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandomProductListResponseFormForUser {
    private Long productId;
    private String productName;
    private String productMainImage;
    private Long minOptionPrice;
}
