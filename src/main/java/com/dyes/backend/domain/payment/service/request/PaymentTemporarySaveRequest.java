package com.dyes.backend.domain.payment.service.request;

import com.dyes.backend.domain.order.service.user.request.OrderedProductOptionRequest;
import com.dyes.backend.domain.order.service.user.request.OrderedPurchaserProfileRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTemporarySaveRequest {
    private String userToken;
    private OrderedPurchaserProfileRequest orderedPurchaserProfileRequest;
    private List<OrderedProductOptionRequest> orderedProductOptionRequestList;
    private int totalAmount;
    private String from;
    private String tid;
    private String partnerOrderId;
    private String partnerUserId;
}
