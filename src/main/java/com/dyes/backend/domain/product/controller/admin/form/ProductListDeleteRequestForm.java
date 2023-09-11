package com.dyes.backend.domain.product.controller.admin.form;

import com.dyes.backend.domain.product.service.admin.request.delete.ProductListDeleteRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDeleteRequestForm {
    private String userToken;
    private List<Long> productIdList = new ArrayList<>();

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public ProductListDeleteRequest toProductListDeleteRequest() {
        return new ProductListDeleteRequest(productIdList);
    }
}
