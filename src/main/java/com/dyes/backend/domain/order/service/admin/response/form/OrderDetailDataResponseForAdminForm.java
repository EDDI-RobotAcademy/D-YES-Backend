package com.dyes.backend.domain.order.service.admin.response.form;

import com.dyes.backend.domain.order.service.admin.response.OrderCombineOrderData;
import com.dyes.backend.domain.order.service.admin.response.OrderCombineOrderedProductData;
import com.dyes.backend.domain.order.service.admin.response.OrderCombineOrderedPurchaserProfileData;
import com.dyes.backend.domain.order.service.admin.response.OrderCombinePaymentData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDataResponseForAdminForm {
    private OrderCombineOrderData orderData;
    private OrderCombinePaymentData paymentData;
    private List<OrderCombineOrderedProductData> productDataList;
    private OrderCombineOrderedPurchaserProfileData profileData;
}
