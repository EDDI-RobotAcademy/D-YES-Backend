package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;

public interface OrderService {
    boolean orderProductInCart(OrderProductInCartRequestForm requestForm);
}
