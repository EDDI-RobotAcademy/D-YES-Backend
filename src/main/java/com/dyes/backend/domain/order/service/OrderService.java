package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.order.controller.form.*;
import com.dyes.backend.domain.order.service.admin.response.form.MonthlyOrdersStatisticsResponseForm;
import com.dyes.backend.domain.order.service.admin.response.form.OrderDetailDataResponseForAdminForm;
import com.dyes.backend.domain.order.service.admin.response.form.OrderInfoResponseFormForDashBoardForAdmin;
import com.dyes.backend.domain.order.service.admin.response.form.OrderListResponseFormForAdmin;
import com.dyes.backend.domain.order.service.user.request.KakaoPaymentRefundProductOptionRequest;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderDetailDataResponseForUserForm;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRefundOrderAndTokenAndReasonRequest;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface OrderService {
    String purchaseReadyWithKakao(OrderProductRequestForm requestForm) throws JsonProcessingException;
    boolean approvalPurchaseWithKakao (KakaoPaymentApprovalRequestForm requestForm) throws JsonProcessingException;
    boolean rejectPurchaseWithKakao (KakaoPaymentRejectRequestForm requestForm);
    boolean refundPurchaseWithKakao (KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest,
                                     List<KakaoPaymentRefundProductOptionRequest> requestList);
    void orderProduct(PaymentTemporarySaveRequest saveRequest);
    OrderConfirmResponseFormForUser orderConfirm(OrderConfirmRequestForm requestForm);
    List<OrderListResponseFormForAdmin> getOrderListForAdmin();
    OrderInfoResponseFormForDashBoardForAdmin getAllNewOrderListForAdmin();
    List<OrderListResponseFormForUser> getMyOrderListForUser(String userToken);
    OrderDetailDataResponseForAdminForm orderDetailDataCombineForAdmin(Long orderId);
    OrderDetailDataResponseForUserForm orderDetailDataCombineForUser(Long orderId);
    MonthlyOrdersStatisticsResponseForm getMonthlyOrders();
}
