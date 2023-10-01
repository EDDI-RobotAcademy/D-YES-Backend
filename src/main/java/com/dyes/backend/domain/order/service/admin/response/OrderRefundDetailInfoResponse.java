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
public class OrderRefundDetailInfoResponse {
    private Long orderId;
    private Long productOrderId;
    private int totalPrice;
    private int cancelPrice;
    private LocalDate orderedTime;
    private DeliveryStatus deliveryStatus;
    private OrderedProductStatus orderedProductStatus;
    private String refundReason;

    public OrderRefundDetailInfoResponse(Long orderId, Long productOrderId, int totalPrice, int cancelPrice, LocalDate orderedTime, DeliveryStatus deliveryStatus, OrderedProductStatus orderedProductStatus) {
        this.orderId = orderId;
        this.productOrderId = productOrderId;
        this.totalPrice = totalPrice;
        this.cancelPrice = cancelPrice;
        this.orderedTime = orderedTime;
        this.deliveryStatus = deliveryStatus;
        this.orderedProductStatus = orderedProductStatus;
    }

    public OrderRefundDetailInfoResponse(Long orderId, Long productOrderId, int totalPrice, int cancelPrice, LocalDate orderedTime, DeliveryStatus deliveryStatus, OrderedProductStatus orderedProductStatus, String refundReason) {
        this.orderId = orderId;
        this.productOrderId = productOrderId;
        this.totalPrice = totalPrice;
        this.cancelPrice = cancelPrice;
        this.orderedTime = orderedTime;
        this.deliveryStatus = deliveryStatus;
        this.orderedProductStatus = orderedProductStatus;
        this.refundReason = refundReason;
    }
}
