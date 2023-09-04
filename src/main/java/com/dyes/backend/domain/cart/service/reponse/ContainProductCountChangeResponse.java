package com.dyes.backend.domain.cart.service.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainProductCountChangeResponse {
    private int changeProductCount;
}
