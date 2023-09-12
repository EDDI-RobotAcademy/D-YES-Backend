package com.dyes.backend.domain.payment.service;

import com.dyes.backend.domain.payment.service.request.KakaoPaymentRequest;
import com.dyes.backend.domain.payment.service.response.KakaoPaymentReadyResponse;

public interface PaymentService {
    KakaoPaymentReadyResponse paymentRequest(KakaoPaymentRequest request);
}
