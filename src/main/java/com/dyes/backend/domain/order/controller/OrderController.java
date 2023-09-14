package com.dyes.backend.domain.order.controller;

import com.dyes.backend.domain.order.controller.form.KakaoPaymentApprovalRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductRequestForm;
import com.dyes.backend.domain.order.service.OrderService;
import com.dyes.backend.domain.order.service.admin.response.form.OrderListResponseFormForAdmin;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    final private OrderService orderService;

    // 카카오로 상품 구매
    @PostMapping("/payment/kakao")
    public RedirectView orderWithKakaoPayment(@RequestBody OrderProductRequestForm requestForm) throws JsonProcessingException {
        return orderService.purchaseReadyWithKakao(requestForm);
    }
    @PostMapping("/payment/kakao/approve")
    public boolean approveWithKakaoPayment(@RequestBody KakaoPaymentApprovalRequestForm requestForm) throws JsonProcessingException {
        return orderService.approvalPurchaseWithKakao(requestForm);
    }

    // 결제 전 주문 요청내역 확인
    @GetMapping("/confirm")
    public OrderConfirmResponseFormForUser confirmProductInCart(@RequestParam("userToken") String userToken) {
        OrderConfirmRequestForm requestForm = new OrderConfirmRequestForm(userToken);
        return orderService.orderConfirm(requestForm);
    }

    // 관리자의 주문 내역 확인
    @GetMapping("/admin/list")
    public List<OrderListResponseFormForAdmin> getAllOrderListForAdmin() {
        return orderService.getOrderListForAdmin();
    }

    // 사용자의 주문 내역 확인
    @GetMapping("/my-list")
    public List<OrderListResponseFormForUser> getMyOrderListForUser(@RequestParam("userToken") String userToken) {
        return orderService.getMyOrderListForUser(userToken);
    }
}
