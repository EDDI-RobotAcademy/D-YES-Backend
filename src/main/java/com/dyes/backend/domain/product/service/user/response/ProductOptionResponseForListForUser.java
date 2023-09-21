package com.dyes.backend.domain.product.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionResponseForListForUser {
    private Long minOptionPrice;
    private Boolean isSoldOut;
}
