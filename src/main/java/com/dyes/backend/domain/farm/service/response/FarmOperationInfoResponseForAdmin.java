package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.farm.entity.FarmBusinessInfo;
import com.dyes.backend.domain.farm.entity.FarmRepresentativeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmOperationInfoResponseForAdmin {
    private Long farmOperationId;
    private String businessName;
    private String businessNumber;
    private String representativeName;
    private String representativeContactNumber;

    public FarmOperationInfoResponseForAdmin farmOperationInfoResponseForAdmin(
            FarmBusinessInfo farmBusinessInfo, FarmRepresentativeInfo farmRepresentativeInfo) {
        return new FarmOperationInfoResponseForAdmin(
                farmBusinessInfo.getId(),
                farmBusinessInfo.getBusinessName(),
                farmBusinessInfo.getBusinessNumber(),
                farmRepresentativeInfo.getRepresentativeName(),
                farmRepresentativeInfo.getRepresentativeContactNumber());
    }
}
