package com.dyes.backend.domain.order.service.user.response;

import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCombineOrderDataForUser {
    private Long productOrderId;
    private DeliveryStatus deliveryStatus;
    private OrderStatus orderStatus;
}
