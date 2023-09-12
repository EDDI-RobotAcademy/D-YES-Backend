package com.dyes.backend.domain.payment.service.response;

import lombok.Data;

@Data
public class KakaoPaymentReadyResponse {
    private String tid;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    private String created_at;
}
