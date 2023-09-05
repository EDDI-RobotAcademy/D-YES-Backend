package com.dyes.backend.domain.order.controller;

import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderConfirmResponseForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductInProductPageRequestForm;
import com.dyes.backend.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    final private OrderService orderService;

    // 장바구니에서 상품 주문
    @PostMapping("/in-cart")
    public boolean orderProductInCart(@RequestBody OrderProductInCartRequestForm requestForm) {
        return orderService.orderProductInCart(requestForm);
    }
    // 제품 페이지에서 상품 주문
    @PostMapping("/in-product-page")
    public boolean orderProductInProductPage(@RequestBody OrderProductInProductPageRequestForm requestForm) {
        return orderService.orderProductInProductPage(requestForm);
    }
    @GetMapping("/confirm")
    public OrderConfirmResponseForm confirmProductInCart(@RequestParam("userToken") String userToken) {
        OrderConfirmRequestForm requestForm = new OrderConfirmRequestForm(userToken);
        return orderService.orderConfirm(requestForm);
    }
}
