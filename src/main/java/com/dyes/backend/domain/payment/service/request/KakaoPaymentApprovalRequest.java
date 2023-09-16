package com.dyes.backend.domain.payment.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentApprovalRequest {
    private String userToken;
    private String pgToken;
}
