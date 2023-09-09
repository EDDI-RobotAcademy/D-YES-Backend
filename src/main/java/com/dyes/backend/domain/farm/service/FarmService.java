package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.farm.controller.form.FarmDeleteRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmModifyRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.response.FarmInfoListResponse;
import com.dyes.backend.domain.farm.service.response.FarmInfoReadResponse;

import java.util.List;

public interface FarmService {

    Boolean registerFarm(FarmRegisterRequestForm registerRequestForm);

    List<FarmInfoListResponse> getFarmList();

    Boolean deleteFarm(Long farmId, FarmDeleteRequestForm deleteRequestForm);

    FarmInfoReadResponse readFarm(Long farmId);

    boolean modifyFarm(Long farmId, FarmModifyRequestForm modifyRequestForm);
}
