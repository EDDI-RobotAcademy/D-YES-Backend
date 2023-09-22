package com.dyes.backend.domain.event.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductPurchaseCountResponse {
    private Integer targetCount;
    private Integer nowCount;
}
