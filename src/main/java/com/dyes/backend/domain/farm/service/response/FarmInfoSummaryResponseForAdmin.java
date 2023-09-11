package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.farm.entity.Farm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoSummaryResponseForAdmin {
    private Long farmId;
    private String farmName;

    public FarmInfoSummaryResponseForAdmin farmInfoSummaryResponseForAdmin(Farm farm) {
        return new FarmInfoSummaryResponseForAdmin(
                farm.getId(), farm.getFarmName());
    }
}