package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.order.entity.OrderedProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCombineOrderedProductData {
    private Long productId;
    private String productName;
    private String productOptionName;
    private Long productOptionId;
    private Long productOptionPrice;
    private Integer productOptionCount;
    private OrderedProductStatus orderedProductStatus;
}
