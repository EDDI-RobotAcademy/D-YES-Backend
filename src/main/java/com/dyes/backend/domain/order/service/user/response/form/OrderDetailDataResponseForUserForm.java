package com.dyes.backend.domain.order.service.user.response.form;

import com.dyes.backend.domain.order.service.user.response.OrderCombineOrderDataForUser;
import com.dyes.backend.domain.order.service.user.response.OrderCombineOrderedProductDataForUser;
import com.dyes.backend.domain.order.service.user.response.OrderCombineOrderedPurchaserProfileDataForUser;
import com.dyes.backend.domain.order.service.user.response.OrderCombinePaymentDataForUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDataResponseForUserForm {
    private OrderCombineOrderDataForUser orderData;
    private OrderCombinePaymentDataForUser paymentData;
    private List<OrderCombineOrderedProductDataForUser> productDataList;
    private OrderCombineOrderedPurchaserProfileDataForUser profileData;
}
