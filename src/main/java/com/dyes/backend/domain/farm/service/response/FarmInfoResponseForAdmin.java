package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmCustomerServiceInfo;
import com.dyes.backend.domain.farm.entity.FarmIntroductionInfo;
import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoResponseForAdmin {
    private Long farmId;
    private String farmName;
    private String csContactNumber;
    private Address farmAddress;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;

    public FarmInfoResponseForAdmin farmInfoResponseForAdmin(
            Farm farm, FarmCustomerServiceInfo farmCustomerServiceInfo, FarmIntroductionInfo farmIntroductionInfo) {
        return new FarmInfoResponseForAdmin(
                farm.getId(),
                farm.getFarmName(), farmCustomerServiceInfo.getCsContactNumber(), farmCustomerServiceInfo.getFarmAddress(),
                farmIntroductionInfo.getMainImage(), farmIntroductionInfo.getIntroduction(), farmIntroductionInfo.getProduceTypes());
    }
}
