package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.farm.controller.form.FarmDeleteForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;

import java.util.List;

public interface FarmService {

    Boolean farmRegister(FarmRegisterRequestForm registerRequestForm);
    List<FarmInfoListResponse> searchFarmList();
    Boolean deleteFarm(FarmDeleteForm deleteForm);
}
