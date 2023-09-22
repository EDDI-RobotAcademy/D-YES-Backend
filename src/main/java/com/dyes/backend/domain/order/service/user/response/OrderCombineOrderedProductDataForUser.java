package com.dyes.backend.domain.order.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCombineOrderedProductDataForUser {
    private String productName;
    private String productOptionName;
    private Long productOptionPrice;
    private Integer productOptionCount;
}
