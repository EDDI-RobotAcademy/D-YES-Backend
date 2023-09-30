package com.dyes.backend.domain.order.service.admin.response.form;

import com.dyes.backend.domain.order.service.admin.response.OrderRefundDetailInfoResponse;
import com.dyes.backend.domain.order.service.admin.response.OrderUserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundListResponseFormForAdmin {
    private OrderUserInfoResponse orderUserInfo;
    private OrderRefundDetailInfoResponse orderRefundDetailInfoResponse;
}
