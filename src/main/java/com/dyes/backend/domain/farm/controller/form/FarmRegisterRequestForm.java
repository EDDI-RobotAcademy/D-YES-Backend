package com.dyes.backend.domain.farm.controller.form;

import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.farm.service.request.*;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmRegisterRequestForm {

    // 관리자 여부 확인용
    private String userToken;

    // 소비자가 보는 농가 정보
    private String farmName;
    private String csContactNumber;
    private String address;
    private String zipCode;
    private String addressDetail;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;

    // 관리자가 보는 농가 정보
    private String businessName;
    private String businessNumber;
    private String representativeName;
    private String representativeContactNumber;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public FarmRegisterRequest toFarmRegisterRequest() {
        return new FarmRegisterRequest(farmName);
    }

    public FarmCustomerServiceInfoRegisterRequest toFarmCustomerServiceInfoRegisterRequest() {
        return new FarmCustomerServiceInfoRegisterRequest(csContactNumber, address, zipCode, addressDetail);
    }

    public FarmIntroductionInfoRegisterRequest toFarmIntroductionInfoRegisterRequest() {
        return new FarmIntroductionInfoRegisterRequest(mainImage, introduction, produceTypes);
    }

    public FarmBusinessInfoRegisterRequest toFarmBusinessInfoRegisterRequest() {
        return new FarmBusinessInfoRegisterRequest(businessName, businessNumber);
    }

    public FarmRepresentativeInfoRegisterRequest toFarmRepresentativeInfoRegisterRequest() {
        return new FarmRepresentativeInfoRegisterRequest(representativeName, representativeContactNumber);
    }
}
