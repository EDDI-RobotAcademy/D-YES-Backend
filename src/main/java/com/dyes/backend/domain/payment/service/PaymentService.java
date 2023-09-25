package com.dyes.backend.domain.payment.service;

import com.dyes.backend.domain.event.entity.EventOrder;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRefundRequestForm;
import com.dyes.backend.domain.order.controller.form.KakaoPaymentRejectRequestForm;
import com.dyes.backend.domain.order.service.user.request.KakaoPaymentRefundProductOptionRequest;
import com.dyes.backend.domain.order.service.user.request.OrderProductRequest;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentApprovalRequest;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRefundOrderAndTokenAndReasonRequest;
import com.dyes.backend.domain.payment.service.request.KakaoPaymentRequest;
import com.dyes.backend.domain.payment.service.request.PaymentTemporarySaveRequest;
import com.dyes.backend.domain.payment.service.response.KakaoPaymentReadyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

public interface PaymentService {
    KakaoPaymentReadyResponse paymentRequest(KakaoPaymentRequest request);
    PaymentTemporarySaveRequest paymentApprovalRequest(KakaoPaymentApprovalRequest request) throws JsonProcessingException;
    String paymentTemporaryDataSaveAndReturnRedirectView (OrderProductRequest request) throws JsonProcessingException;
    boolean paymentRejectWithKakao(KakaoPaymentRejectRequestForm requestForm);
    boolean paymentRefundRequest(KakaoPaymentRefundOrderAndTokenAndReasonRequest orderAndTokenAndReasonRequest,
                                 List<KakaoPaymentRefundProductOptionRequest> requestList);
    void paymentEventProductRefundRequest(EventOrder eventOrder);
}
