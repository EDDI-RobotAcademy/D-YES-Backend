package com.dyes.backend.domain.order.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductListResponseForUser {
    private Long productId;
    private String productName;
    private List<OrderOptionListResponse> orderOptionList;
    private Long reviewId;
}
