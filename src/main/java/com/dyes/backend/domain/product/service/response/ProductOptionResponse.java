package com.dyes.backend.domain.product.service.response;

import com.dyes.backend.domain.product.entity.ProductOption;
import com.dyes.backend.domain.product.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionResponse {
    public Long optionId;
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private Unit unit;

    public ProductOptionResponse (ProductOption productOption) {
        this.optionId = productOption.getId();
        this.optionName = productOption.getOptionName();
        this.optionPrice = productOption.getOptionPrice();
        this.stock = productOption.getStock();
        this.value = productOption.getAmount().getValue();
        this.unit = productOption.getAmount().getUnit();
    }
    public List<ProductOptionResponse> productOptionResponseList(List<ProductOption> productOptionList) {
        return productOptionList.stream()
                .map(ProductOptionResponse::new)
                .collect(Collectors.toList());
    }
}
