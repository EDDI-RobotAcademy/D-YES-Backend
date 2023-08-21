package com.dyes.backend.domain.product.service;

import com.dyes.backend.domain.product.controller.form.ProductRegisterForm;

public interface ProductService {
    boolean productRegistration(ProductRegisterForm registerForm);
}
