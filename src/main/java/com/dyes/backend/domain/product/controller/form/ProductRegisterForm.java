package com.dyes.backend.domain.product.controller.form;

import com.dyes.backend.domain.product.service.request.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.request.ProductRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterForm {
    private String userToken;
    private ProductRegisterRequest productRegisterRequest;
    private List<ProductOptionRegisterRequest> productOptionRegisterRequest;
    private ProductMainImageRegisterRequest productMainImageRegisterRequest;
    private List<ProductDetailImagesRegisterRequest> productDetailImagesRegisterRequests;
}
