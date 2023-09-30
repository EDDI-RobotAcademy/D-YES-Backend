package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.delivery.entity.DeliveryStatus;
import com.dyes.backend.domain.order.entity.OrderStatus;
import com.dyes.backend.domain.order.entity.OrderedProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundDetailInfoResponse {
    private Long productOrderId;
    private int totalPrice;
    private int cancelPrice;
    private LocalDate orderedTime;
    private DeliveryStatus deliveryStatus;
    private OrderedProductStatus orderedProductStatus;
}
