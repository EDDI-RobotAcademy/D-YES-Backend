package com.dyes.backend.domain.order.service.response;

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
}
