package com.dyes.backend.domain.cart.service;

import com.dyes.backend.domain.cart.controller.form.ContainProductModifyRequestForm;
import com.dyes.backend.domain.cart.controller.form.ContainProductRequestForm;

public interface CartService {
    void containProductIntoCart(ContainProductRequestForm requestForm);
    void changeProductOptionCount(ContainProductModifyRequestForm requestForm);
}
