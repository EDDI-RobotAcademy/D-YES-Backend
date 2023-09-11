package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.CultivationMethod;
import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseForAdmin {
    private Long productId;
    private String productName;
    private String productDescription;
    private CultivationMethod cultivationMethod;
    private SaleStatus productSaleStatus;

    public ProductResponseForAdmin productResponseForAdmin(Product product) {
        return new ProductResponseForAdmin(
                product.getId(),
                product.getProductName(),
                product.getProductDescription(),
                product.getCultivationMethod(),
                product.getProductSaleStatus());
    }
}
