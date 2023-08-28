package com.dyes.backend.domain.product.service.response.admin;

import com.dyes.backend.domain.farm.entity.FarmOperation;
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

    public FarmOperationInfoResponseForAdmin farmOperationInfoResponseForAdmin (FarmOperation farmOperation) {
        return new FarmOperationInfoResponseForAdmin(
                farmOperation.getId(),
                farmOperation.getBusinessName(),
                farmOperation.getBusinessNumber(),
                farmOperation.getRepresentativeName(),
                farmOperation.getRepresentativeContactNumber());
    }
}
