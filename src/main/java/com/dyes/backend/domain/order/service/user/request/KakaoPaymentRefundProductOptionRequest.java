package com.dyes.backend.domain.order.service.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentRefundProductOptionRequest {
    private Long productOptionId;
}
