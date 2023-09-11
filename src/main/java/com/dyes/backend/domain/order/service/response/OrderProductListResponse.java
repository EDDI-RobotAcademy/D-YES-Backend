package com.dyes.backend.domain.order.service.response;

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
