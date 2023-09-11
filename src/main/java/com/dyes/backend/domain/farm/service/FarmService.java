package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.farm.controller.form.FarmDeleteRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmModifyRequestForm;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.service.response.form.FarmInfoListResponseForm;
import com.dyes.backend.domain.farm.service.response.form.FarmInfoReadResponseForm;

import java.util.List;

public interface FarmService {

    Boolean registerFarm(FarmRegisterRequestForm registerRequestForm);

    List<FarmInfoListResponseForm> getFarmList();

    Boolean deleteFarm(Long farmId, FarmDeleteRequestForm deleteRequestForm);

    FarmInfoReadResponseForm readFarm(Long farmId);

    boolean modifyFarm(Long farmId, FarmModifyRequestForm modifyRequestForm);
}
