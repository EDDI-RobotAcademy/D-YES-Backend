package com.dyes.backend.domain.product.service.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseFormForAdmin {
    private ProductResponseForAdmin productResponseForAdmin;
    private List<ProductOptionResponseForAdmin> optionResponseForAdmin;
    private ProductMainImageResponseForAdmin mainImageResponseForAdmin;
    private List<ProductDetailImagesResponseForAdmin> detailImagesForAdmin;
    private FarmInfoResponseForAdmin farmInfoResponseForAdmin;
    private FarmOperationInfoResponseForAdmin farmOperationInfoResponseForAdmin;
}
