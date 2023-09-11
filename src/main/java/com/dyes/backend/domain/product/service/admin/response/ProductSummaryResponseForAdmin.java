package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.Product;
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

    public ProductSummaryResponseForAdmin productSummaryResponseForAdmin(Product product) {
        return new ProductSummaryResponseForAdmin(
                product.getId(),
                product.getProductName(),
                product.getProductSaleStatus());
    }
}
