package com.dyes.backend.domain.farm.controller.form;

import com.dyes.backend.domain.farm.service.request.FarmRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmRegisterRequestForm {
    private String userToken;
    private String farmName;
    private String farmOwnerName;
    private String contactNumber;
    private String address;
    private String zipCode;
    private String addressDetail;

    public FarmRegisterRequest toFarmRegisterRequest () {
        return new FarmRegisterRequest(farmName, farmOwnerName, contactNumber, address, zipCode, addressDetail);
    }
}
