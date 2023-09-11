package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.farm.entity.*;
import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoResponseForUser {
    private String farmName;
    private String csContactNumber;
    private Address farmAddress;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;

    public FarmInfoResponseForUser farmInfoResponse(Farm farm, FarmCustomerServiceInfo farmCustomerServiceInfo, FarmIntroductionInfo farmIntroductionInfo) {
        return new FarmInfoResponseForUser(
                farm.getFarmName(), farmCustomerServiceInfo.getCsContactNumber(), farmCustomerServiceInfo.getFarmAddress(),
                farmIntroductionInfo.getMainImage(), farmIntroductionInfo.getIntroduction(), farmIntroductionInfo.getProduceTypes());
    }
}
