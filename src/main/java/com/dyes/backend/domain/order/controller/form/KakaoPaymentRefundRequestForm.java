package com.dyes.backend.domain.order.controller.form;

import com.dyes.backend.domain.order.service.user.request.KakaoPaymentRefundProductOptionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentRefundRequestForm {
    private String userToken;
    private Long orderId;
    private List<KakaoPaymentRefundProductOptionRequest> requestList;
}
