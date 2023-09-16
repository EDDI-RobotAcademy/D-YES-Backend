package com.dyes.backend.domain.order.service.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductRequest {
    private String userToken;
    private OrderedPurchaserProfileRequest orderedPurchaserProfileRequest;
    private List<OrderedProductOptionRequest> orderedProductOptionRequestList;
    private int totalAmount;
    private String from;
}
