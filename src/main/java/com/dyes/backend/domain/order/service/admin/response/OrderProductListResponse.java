package com.dyes.backend.domain.order.service.admin.response;

import com.dyes.backend.domain.order.service.user.response.OrderOptionListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductListResponse {
    private Long productId;
    private String productName;
    private List<OrderOptionListResponse> orderOptionList;
}
