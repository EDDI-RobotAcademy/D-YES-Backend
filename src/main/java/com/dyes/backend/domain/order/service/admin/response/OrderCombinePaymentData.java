package com.dyes.backend.domain.order.service.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCombinePaymentData {
    private Integer totalPrice;
    private Integer deliveryFee;
    private Integer paymentPrice;
    private String paymentMethod;
    private LocalDate paymentDate;
}
