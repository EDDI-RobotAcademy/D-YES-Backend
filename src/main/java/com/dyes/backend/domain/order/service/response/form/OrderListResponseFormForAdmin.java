package com.dyes.backend.domain.order.service.response.form;

import com.dyes.backend.domain.order.service.response.OrderProductListResponse;
import com.dyes.backend.domain.order.service.response.OrderUserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponseFormForAdmin {
    private String productOrderId;
    private OrderUserInfoResponse orderUserInfo;
    private List<OrderProductListResponse> orderProductList;
    private Long totalPrice;
}
