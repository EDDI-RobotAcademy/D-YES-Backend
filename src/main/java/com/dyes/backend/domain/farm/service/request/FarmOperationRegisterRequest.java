package com.dyes.backend.domain.farm.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmOperationRegisterRequest {
    private String businessName;
    private String businessNumber;
    private String representativeName;
    private String representativeContactNumber;
}
