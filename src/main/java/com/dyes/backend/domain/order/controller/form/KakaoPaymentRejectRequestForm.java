package com.dyes.backend.domain.order.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaymentRejectRequestForm {
    private String userToken;
}
