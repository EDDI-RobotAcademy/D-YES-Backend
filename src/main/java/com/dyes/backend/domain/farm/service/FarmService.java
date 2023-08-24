package com.dyes.backend.domain.farm.service;

import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;

public interface FarmService {

    Boolean farmRegister(FarmRegisterRequestForm registerRequestForm);
}
