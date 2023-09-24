package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoResponseForAdmin {
    private Long productOrderId;
    private String productName;
    private OrderStatus orderStatus;
    private LocalDate orderedTime;
}
