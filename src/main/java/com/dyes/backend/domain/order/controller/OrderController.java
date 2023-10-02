package com.dyes.backend.domain.order.controller;

import com.dyes.backend.domain.order.controller.form.*;
import com.dyes.backend.domain.order.service.OrderService;
import com.dyes.backend.domain.order.service.admin.response.OrderProductListResponse;
import com.dyes.backend.domain.order.service.admin.response.form.*;
import com.dyes.backend.domain.order.service.user.request.KakaoPaymentRefundProductOptionRequest;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderDetailDataResponseForUserForm;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRefundOrderAndTokenAndReasonRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest = requestForm.getOrderAndTokenAndReasonRequest();
        List<KakaoPaymentRefundProductOptionRequest> requestList = requestForm.getRequestList();
        return orderService.refundPurchaseWithKakao(orderAndTokenAndReasonRequest, requestList);
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
    public OrderInfoResponseFormForDashBoardForAdmin getAllNewOrderListForAdmin() {
        return orderService.getAllNewOrderListForAdmin();
    }

    // 사용자의 주문 내역 확인
    @GetMapping("/my-list")
    public List<OrderListResponseFormForUser> getMyOrderListForUser(@RequestParam("userToken") String userToken) {
        return orderService.getMyOrderListForUser(userToken);
    }

    // 관리자의 주문 내역 상세 읽기
    @GetMapping("/admin/combine-order-data/{productOrderId}")
    public OrderDetailDataResponseForAdminForm getCombineOrderData(@PathVariable("productOrderId") Long productOrderId){
        return orderService.orderDetailDataCombineForAdmin(productOrderId);
    }

    // 사용자의 주문 내역 상세 읽기
    @GetMapping("/user/combine-order-data/{productOrderId}")
    public OrderDetailDataResponseForUserForm getCombineOrderDataForUser(@PathVariable("productOrderId") Long productOrderId){
        return orderService.orderDetailDataCombineForUser(productOrderId);
    }

    // 배송완료 주문은 환불 신청시 환불 대기 상태로 변경
    @PostMapping("/waiting-for-refund")
    public boolean waitingForRefund (@RequestBody OrderedProductChangeStatusRequestForm requestForm){
        return orderService.orderedProductWaitingRefund(requestForm);
    }

    // 관리자의 월 주문 통계 데이터 확인
    @GetMapping("/admin/monthly_orders")
    public MonthlyOrdersStatisticsResponseForm getMonthlyOrders(){
        return orderService.getMonthlyOrders();
    }

    // 관리자의 환불 목록 확인
    @GetMapping("/admin/refund-list")
    public List<OrderRefundListResponseFormForAdmin> getAllOrderRefundListForAdmin() {
        return orderService.getAllOrderRefundListForAdmin();
    }

    // 관리자의 환불 주문건의 간략한 정보 확인
    @GetMapping("/admin/refund/read/{productOrderId}")
    public OrderProductListResponse getRefundSummaryInfo(@PathVariable("productOrderId") Long productOrderId) {
        return orderService.getRefundSummaryInfo(productOrderId);
    }
}
