package com.dyes.backend.domain.product.service.response.admin;

import com.dyes.backend.domain.product.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponseForAdmin {
    private Long productId;
    private String productName;
    private SaleStatus productSaleStatus;
}
