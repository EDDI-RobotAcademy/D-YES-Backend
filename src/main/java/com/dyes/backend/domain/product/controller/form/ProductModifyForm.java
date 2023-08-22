package com.dyes.backend.domain.product.controller.form;

import com.dyes.backend.domain.product.service.request.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModifyForm {
    private ProductModifyRequest productModifyRequest;
    private List<ProductOptionModifyRequest> productOptionModifyRequest;
    private ProductMainImageModifyRequest productMainImageModifyRequest;
    private List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest;
}
