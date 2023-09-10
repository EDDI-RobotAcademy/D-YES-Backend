package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionListResponseForAdmin {
    private String optionName;
    private Long optionPrice;
    private int stock;
    private SaleStatus optionSaleStatus;

    public ProductOptionListResponseForAdmin productOptionListResponseForAdmin(ProductOption productOption) {
        return new ProductOptionListResponseForAdmin(
                productOption.getOptionName(),
                productOption.getOptionPrice(),
                productOption.getStock(),
                productOption.getOptionSaleStatus());
    }
}
