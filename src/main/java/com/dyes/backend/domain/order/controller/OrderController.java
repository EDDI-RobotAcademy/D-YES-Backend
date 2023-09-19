package com.dyes.backend.domain.order.controller;

import com.dyes.backend.domain.order.controller.form.*;
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
    public String orderWithKakaoPayment(@RequestBody OrderProductRequestForm requestForm) throws JsonProcessingException {
        return orderService.purchaseReadyWithKakao(requestForm);
    }
    @PostMapping("/payment/kakao/approve")
    public boolean approveWithKakaoPayment(@RequestBody KakaoPaymentApprovalRequestForm requestForm) throws JsonProcessingException {
        return orderService.approvalPurchaseWithKakao(requestForm);
    }
    @DeleteMapping("/payment/kakao/reject")
    public boolean rejectWithKakaoPayment(@RequestBody KakaoPaymentRejectRequestForm requestForm) {
        return orderService.rejectPurchaseWithKakao(requestForm);
    }
    @PostMapping("/payment/kakao/refund")
    public boolean refundWithKakaoPayment(@RequestBody KakaoPaymentRefundRequestForm requestForm) {
        log.info("KakaoPaymentRefundRequestForm: " + requestForm);
        return orderService.refundPurchaseWithKakao(requestForm);
    }

    // 결제 전 주문 요청내역 확인
    @PostMapping("/confirm")
    public OrderConfirmResponseFormForUser confirmProductInCart(@RequestBody OrderConfirmRequestForm requestForm) {
        return orderService.orderConfirm(requestForm);
    }

    // 관리자의 주문 내역 확인
    @GetMapping("/admin/list")
    public List<OrderListResponseFormForAdmin> getAllOrderListForAdmin() {
        return orderService.getOrderListForAdmin();
    }

    // 관리자의 신규 주문 내역 확인
    @GetMapping("/admin/new-list")
    public List<OrderListResponseFormForAdmin> getAllNewOrderListForAdmin() {
        return orderService.getAllNewOrderListForAdmin();
    }

    // 사용자의 주문 내역 확인
    @GetMapping("/my-list")
    public List<OrderListResponseFormForUser> getMyOrderListForUser(@RequestParam("userToken") String userToken) {
        return orderService.getMyOrderListForUser(userToken);
    }
}
