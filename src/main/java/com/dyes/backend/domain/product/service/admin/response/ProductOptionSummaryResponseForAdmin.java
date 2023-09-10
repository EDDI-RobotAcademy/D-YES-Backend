package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionSummaryResponseForAdmin {
    public Long optionId;
    private String optionName;
    private Long optionPrice;
    private int stock;
    private SaleStatus optionSaleStatus;

    public ProductOptionSummaryResponseForAdmin productOptionSummaryResponseForAdmin(ProductOption productOption) {
        return new ProductOptionSummaryResponseForAdmin(
                productOption.getId(),
                productOption.getOptionName(),
                productOption.getOptionPrice(),
                productOption.getStock(),
                productOption.getOptionSaleStatus());
    }
}
