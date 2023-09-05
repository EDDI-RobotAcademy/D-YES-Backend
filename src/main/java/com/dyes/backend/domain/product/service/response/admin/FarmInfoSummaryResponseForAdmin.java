package com.dyes.backend.domain.product.service.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoSummaryResponseForAdmin {
    private Long farmId;
    private String farmName;
}