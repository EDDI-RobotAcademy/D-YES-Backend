package com.dyes.backend.domain.payment.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentReadyResponse {
    private String tid;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    private String created_at;
}
