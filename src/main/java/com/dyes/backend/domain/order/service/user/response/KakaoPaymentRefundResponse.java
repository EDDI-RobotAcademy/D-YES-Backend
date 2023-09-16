package com.dyes.backend.domain.order.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentRefundResponse {
    private String aid;
    private String tid;
    private String cid;
    private String status;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private KakaoRefundAmountRequest amount;
    private KakaoRefundApprovedCancelAmountRequest approved_cancel_amount;
    private KakaoRefundCanceledAmountRequest canceled_amount;
    private KakaoRefundCancelAvailableAmountRequest cancel_available_amount;
    private String item_name;
    private String item_code;
    private Integer quantity;
    private LocalDate created_at;
    private LocalDate approved_at;
    private LocalDate canceled_at;
    private String payload;
}
