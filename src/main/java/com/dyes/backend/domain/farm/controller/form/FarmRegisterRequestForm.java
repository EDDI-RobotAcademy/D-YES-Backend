package com.dyes.backend.domain.farm.controller.form;

import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.farm.service.request.FarmOperationRegisterRequest;
import com.dyes.backend.domain.farm.service.request.FarmRegisterRequest;
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
    private String CSContactNumber;
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

    public FarmRegisterRequest toFarmRegisterRequest () {
        return new FarmRegisterRequest(farmName, CSContactNumber, address, zipCode, addressDetail, mainImage, introduction, produceTypes);
    }

    public FarmOperationRegisterRequest toFarmOperationRegisterRequest () {
        return new FarmOperationRegisterRequest(businessName, businessNumber, representativeName, representativeContactNumber);
    }
}
