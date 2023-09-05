package com.dyes.backend.domain.order.controller;

import com.dyes.backend.domain.order.controller.form.OrderProductInCartRequestForm;
import com.dyes.backend.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
