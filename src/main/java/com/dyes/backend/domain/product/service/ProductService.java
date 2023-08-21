package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductModifyForm;
import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;
import com.dyes.backend.domain.product.service.Response.ProductResponseForm;

public interface ProductService {
    boolean productRegistration(ProductRegisterForm registerForm);
    ProductResponseForm readProduct(Long productId);
    boolean productModify(ProductModifyForm modifyForm);
}
