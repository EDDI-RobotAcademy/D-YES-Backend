package com.dyes.backend.domain.product.service.response;

import com.dyes.backend.domain.product.entity.CultivationMethod;
import com.dyes.backend.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productName;
    private String productDescription;
    private CultivationMethod cultivationMethod;

    public ProductResponse productResponse(Product product) {
        return new ProductResponse(product.getId(), product.getProductName(), product.getProductDescription(), product.getCultivationMethod());
    }
}
