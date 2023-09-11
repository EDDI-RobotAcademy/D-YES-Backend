package com.dyes.backend.domain.product.controller.admin.form;

import com.dyes.backend.domain.farm.service.request.FarmAuthenticationRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductDetailImagesRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductMainImageRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductOptionRegisterRequest;
import com.dyes.backend.domain.product.service.admin.request.register.ProductRegisterRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterRequestForm {
    private String userToken;
    private ProductRegisterRequest productRegisterRequest;
    private List<ProductOptionRegisterRequest> productOptionRegisterRequest;
    private ProductMainImageRegisterRequest productMainImageRegisterRequest;
    private List<ProductDetailImagesRegisterRequest> productDetailImagesRegisterRequests;
    private String farmName;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public FarmAuthenticationRequest toFarmAuthenticationRequest() {
        return new FarmAuthenticationRequest(farmName);
    }
}
