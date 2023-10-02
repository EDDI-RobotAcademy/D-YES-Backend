package com.dyes.backend.domain.order.service.user.response;

import com.dyes.backend.domain.order.entity.OrderedProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOptionListResponse {
    private Long optionId;
    private String optionName;
    private int optionCount;
    private OrderedProductStatus orderProductStatus;
}
