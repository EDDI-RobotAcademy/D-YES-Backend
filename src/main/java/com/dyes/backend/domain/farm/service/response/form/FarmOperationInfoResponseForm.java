package com.dyes.backend.domain.farm.service.response.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmOperationInfoResponseForm {
    private Long farmOperationId;
    private String businessName;
    private String businessNumber;
    private String representativeName;
    private String representativeContactNumber;
}
