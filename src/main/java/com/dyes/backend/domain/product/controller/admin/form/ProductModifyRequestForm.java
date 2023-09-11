package com.dyes.backend.domain.product.controller.admin.form;

import com.dyes.backend.domain.product.service.admin.request.modify.ProductDetailImagesModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductMainImageModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductModifyRequest;
import com.dyes.backend.domain.product.service.admin.request.modify.ProductOptionModifyRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModifyRequestForm {
    private String userToken;
    private ProductModifyRequest productModifyRequest;
    private List<ProductOptionModifyRequest> productOptionModifyRequest;
    private ProductMainImageModifyRequest productMainImageModifyRequest;
    private List<ProductDetailImagesModifyRequest> productDetailImagesModifyRequest;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }
}
