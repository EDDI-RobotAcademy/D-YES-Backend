package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.order.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailInfoResponse {
    private String productOrderId;
    private Long totalPrice;
    private LocalDate orderedTime;
    private DeliveryStatus deliveryStatus;
}
