package com.dyes.backend.domain.product.service.admin.response.form;

import com.dyes.backend.domain.product.entity.SaleStatus;
import com.dyes.backend.domain.product.service.admin.response.ProductOptionListResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseFormForAdmin {
    private Long productId;
    private String productName;
    private SaleStatus productSaleStatus;
    private List<ProductOptionListResponseForAdmin> productOptionList;
    private String farmName;
}
