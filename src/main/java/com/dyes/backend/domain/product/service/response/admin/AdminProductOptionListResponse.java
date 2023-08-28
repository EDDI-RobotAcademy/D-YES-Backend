package com.dyes.backend.domain.product.service.response.admin;

import com.dyes.backend.domain.product.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductOptionListResponse {
    private String optionName;
    private Long optionPrice;
    private int stock;
    private SaleStatus optionSaleStatus;
}
