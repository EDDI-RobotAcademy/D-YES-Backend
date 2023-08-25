package com.dyes.backend.domain.farm.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmRegisterRequest {
    private String farmName;
    private String farmOwnerName;
    private String contactNumber;
    private String address;
    private String zipCode;
    private String addressDetail;
}
