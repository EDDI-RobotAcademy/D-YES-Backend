package com.dyes.backend.domain.order.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedProductChangeStatusRequestForm {
    private String userToken;
    private Long orderId;
    private List<Long> productOptionId;
    private String refundReason;
}
