package com.dyes.backend.domain.event.service.request.modify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductModifyPurchaseCountRequest {
    private Integer targetCount;

}
