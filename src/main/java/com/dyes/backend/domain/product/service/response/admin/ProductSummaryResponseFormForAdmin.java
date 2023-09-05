package com.dyes.backend.domain.product.service.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponseFormForAdmin {
    private ProductSummaryResponseForAdmin productSummaryResponseForAdmin;
    private List<ProductOptionSummaryResponseForAdmin> optionSummaryResponseForAdmin;
    private FarmInfoSummaryResponseForAdmin farmInfoSummaryResponseForAdmin;
}
