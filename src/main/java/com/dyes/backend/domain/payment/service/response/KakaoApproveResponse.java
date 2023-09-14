package com.dyes.backend.domain.payment.service.response;

import com.dyes.backend.domain.payment.entity.PaymentAmount;
import com.dyes.backend.domain.payment.entity.PaymentCardInfo;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    @Embedded
    private KakaoApproveAmountResponse amount;
    @Embedded
    private KakaoApproveCardInfoResponse card_info;
    private String item_name;
    private int quantity;
    private LocalDate created_at;
    private LocalDate approved_at;
}
