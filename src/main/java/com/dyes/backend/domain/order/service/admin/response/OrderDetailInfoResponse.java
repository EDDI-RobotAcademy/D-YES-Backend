package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailInfoResponse {
    private Long productOrderId;
    private Long totalPrice;
    private LocalDate orderedTime;
    private DeliveryStatus deliveryStatus;
    private OrderStatus orderStatus;
}
