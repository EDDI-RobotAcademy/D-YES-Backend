package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;

public interface OrderService {
    boolean orderProductInCart(OrderProductInCartRequestForm requestForm);
    boolean orderProductInProductPage(OrderProductInProductPageRequestForm requestForm);
}
