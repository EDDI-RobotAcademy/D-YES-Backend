package com.dyes.backend.domain.order.service.admin.response;

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
public class OrderCombineOrderData {
    private Long productOrderId;
    private DeliveryStatus deliveryStatus;
    private OrderStatus orderStatus;
}
