package com.dyes.backend.domain.event.service.response;

import com.dyes.backend.domain.farm.entity.ProduceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventProductProduceTypeResponse {
    private ProduceType productType;
}
