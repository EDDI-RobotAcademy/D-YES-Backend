package com.dyes.backend.domain.product.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRandomProductListResponseForm {
    private Long productId;
    private String productName;
    private String productMainImage;
    private Long minOptionPrice;
}
