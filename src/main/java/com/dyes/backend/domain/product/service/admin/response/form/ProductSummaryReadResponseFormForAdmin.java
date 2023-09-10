package com.dyes.backend.domain.product.service.admin.response.form;

import com.dyes.backend.domain.farm.service.response.FarmInfoSummaryResponseForAdmin;
import com.dyes.backend.domain.product.service.admin.response.ProductOptionSummaryResponseForAdmin;
import com.dyes.backend.domain.product.service.admin.response.ProductSummaryResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryReadResponseFormForAdmin {
    private ProductSummaryResponseForAdmin productSummaryResponseForAdmin;
    private List<ProductOptionSummaryResponseForAdmin> optionSummaryResponseForAdmin;
    private FarmInfoSummaryResponseForAdmin farmInfoSummaryResponseForAdmin;
}
