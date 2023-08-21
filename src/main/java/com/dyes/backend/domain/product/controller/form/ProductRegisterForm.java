package com.dyes.backend.domain.product.controller.form;

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
    private String productName;
    private String productDescription;
    private String cultivationMethod;
    private List<ProductOptionRegisterRequest> productOptionRegisterRequest;
    private String mainImg;
    private List<String> detailImgs;
    public ProductRegisterRequest toProductRegister () {
        return new ProductRegisterRequest(productName, productDescription, cultivationMethod, productOptionRegisterRequest, mainImg, detailImgs);
    }
}
