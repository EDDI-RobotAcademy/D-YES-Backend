package com.dyes.backend.domain.order.service.user.response.form;

import com.dyes.backend.domain.order.service.admin.response.OrderDetailInfoResponse;
import com.dyes.backend.domain.order.service.admin.response.OrderProductListResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponseFormForUser {
    private List<OrderProductListResponse> orderProductList;
    private OrderDetailInfoResponse orderDetailInfoResponse;
}
