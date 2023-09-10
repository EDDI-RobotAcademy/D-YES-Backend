package com.dyes.backend.domain.product.service.admin.response;

import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.SaleStatus;
import com.dyes.backend.domain.product.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionResponseForAdmin {
    public Long optionId;
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private Unit unit;
    private SaleStatus optionSaleStatus;

    public ProductOptionResponseForAdmin(ProductOption productOption) {
        this.optionId = productOption.getId();
        this.optionName = productOption.getOptionName();
        this.optionPrice = productOption.getOptionPrice();
        this.stock = productOption.getStock();
        this.value = productOption.getAmount().getValue();
        this.unit = productOption.getAmount().getUnit();
        this.optionSaleStatus = productOption.getOptionSaleStatus();
    }

    public List<ProductOptionResponseForAdmin> productOptionResponseForAdmin(List<ProductOption> productOptionList) {
        return productOptionList.stream()
                .map(ProductOptionResponseForAdmin::new)
                .collect(Collectors.toList());
    }
}
