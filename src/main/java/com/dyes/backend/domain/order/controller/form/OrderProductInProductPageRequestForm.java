package com.dyes.backend.domain.order.controller.form;

import com.dyes.backend.domain.order.service.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.request.OrderedPurchaserProfileRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductInProductPageRequestForm {
    private String userToken;
    private OrderedPurchaserProfileRequest orderedPurchaserProfileRequest;
    private List<OrderedProductOptionRequest> orderedProductOptionRequestList;
    private int totalAmount;
}
