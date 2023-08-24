package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.Response.AdminProductListResponseForm;
import com.dyes.backend.domain.product.service.Response.ProductResponseForm;
import com.dyes.backend.domain.product.service.Response.UserProductListResponseForm;

import java.util.List;

public interface ProductService {
    boolean productRegistration(ProductRegisterForm registerForm);
    ProductResponseForm readProduct(Long productId);
    boolean productModify(ProductModifyForm modifyForm);
    boolean productDelete(Long productId);
    List<AdminProductListResponseForm> getAdminProductList(String userToken);
    List<UserProductListResponseForm> getUserProductList();
}
