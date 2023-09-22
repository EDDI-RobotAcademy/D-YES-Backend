package com.dyes.backend.domain.event.service.request.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductRegisterPurchaseCountRequest {
    private Integer targetCount;

}
