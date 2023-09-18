package com.dyes.backend.domain.order.service;

import com.dyes.backend.domain.order.controller.form.KakaoPaymentApprovalRequestForm;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRejectRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderConfirmRequestForm;
import com.dyes.backend.domain.order.controller.form.OrderProductRequestForm;
import com.dyes.backend.domain.order.service.admin.response.form.OrderListResponseFormForAdmin;
import com.dyes.backend.domain.order.service.user.response.form.OrderConfirmResponseFormForUser;
import com.dyes.backend.domain.order.service.user.response.form.OrderListResponseFormForUser;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

public interface OrderService {
    RedirectView purchaseReadyWithKakao(OrderProductRequestForm requestForm) throws JsonProcessingException;
    boolean approvalPurchaseWithKakao (KakaoPaymentApprovalRequestForm requestForm) throws JsonProcessingException;
    boolean rejectPurchaseWithKakao (KakaoPaymentRejectRequestForm requestForm);
    void orderProduct(PaymentTemporarySaveRequest saveRequest);

    OrderConfirmResponseFormForUser orderConfirm(OrderConfirmRequestForm requestForm);

    List<OrderListResponseFormForAdmin> getOrderListForAdmin();

    List<OrderListResponseFormForUser> getMyOrderListForUser(String userToken);
}
