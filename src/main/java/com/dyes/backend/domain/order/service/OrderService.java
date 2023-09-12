package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.admin.response.form.OrderListResponseFormForAdmin;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

public interface OrderService {
    RedirectView purchaseReadyWithKakao(OrderProductInCartRequestForm requestForm);
    boolean orderProductInCart(OrderProductInCartRequestForm requestForm);

    boolean orderProductInProductPage(OrderProductInProductPageRequestForm requestForm);

    OrderConfirmResponseFormForUser orderConfirm(OrderConfirmRequestForm requestForm);

    List<OrderListResponseFormForAdmin> getOrderListForAdmin();

    List<OrderListResponseFormForUser> getMyOrderListForUser(String userToken);
}
