package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.farm.controller.form.FarmDeleteForm;
import com.dyes.backend.domain.farm.controller.form.FarmModifyForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoReadResponse;

import java.util.List;

public interface FarmService {

    Boolean farmRegister(FarmRegisterRequestForm registerRequestForm);
    List<FarmInfoListResponse> searchFarmList();
    Boolean deleteFarm(Long farmId, FarmDeleteForm deleteForm);
    FarmInfoReadResponse readFarmInfo(Long farmId);
    boolean farmModify(Long farmId, FarmModifyForm modifyForm);
}
