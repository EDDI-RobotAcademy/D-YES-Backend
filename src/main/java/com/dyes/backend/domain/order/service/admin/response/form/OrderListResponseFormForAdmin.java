package com.dyes.backend.domain.order.service.admin.response.form;

import com.dyes.backend.domain.order.service.admin.response.OrderDetailInfoResponse;
import com.dyes.backend.domain.order.service.admin.response.OrderProductListResponse;
import com.dyes.backend.domain.order.service.admin.response.OrderUserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponseFormForAdmin {
    private OrderUserInfoResponse orderUserInfo;
    private List<OrderProductListResponse> orderProductList;
    private OrderDetailInfoResponse orderDetailInfoResponse;
}
