package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;
import com.dyes.backend.domain.order.service.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.response.form.OrderListResponseFormForAdmin;

import java.util.List;

public interface OrderService {
    boolean orderProductInCart(OrderProductInCartRequestForm requestForm);

    boolean orderProductInProductPage(OrderProductInProductPageRequestForm requestForm);

    OrderConfirmResponseFormForUser orderConfirm(OrderConfirmRequestForm requestForm);

    List<OrderListResponseFormForAdmin> getOrderListForAdmin();
}
