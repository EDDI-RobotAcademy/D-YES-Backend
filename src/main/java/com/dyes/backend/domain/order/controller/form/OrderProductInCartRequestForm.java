package com.dyes.backend.domain.order.controller.form;

import com.dyes.backend.domain.order.service.user.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedPurchaserProfileRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductInCartRequestForm {
    private String userToken;
    private OrderedPurchaserProfileRequest orderedPurchaserProfileRequest;
    private List<OrderedProductOptionRequest> orderedProductOptionRequestList;
    private int totalAmount;
}
