package com.dyes.backend.domain.farm.controller.form;

import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.farm.service.request.FarmCustomerServiceInfoModifyRequest;
import com.dyes.backend.domain.farm.service.request.FarmIntroductionInfoModifyRequest;
import com.dyes.backend.domain.user.service.request.UserAuthenticationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmModifyRequestForm {
    private String userToken;
    private String csContactNumber;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;

    public UserAuthenticationRequest toUserAuthenticationRequest() {
        return new UserAuthenticationRequest(userToken);
    }

    public FarmCustomerServiceInfoModifyRequest toFarmCustomerServiceInfoModifyRequest() {
        return new FarmCustomerServiceInfoModifyRequest(csContactNumber);
    }

    public FarmIntroductionInfoModifyRequest toFarmIntroductionInfoModifyRequest() {
        return new FarmIntroductionInfoModifyRequest(mainImage, introduction, produceTypes);
    }
}
