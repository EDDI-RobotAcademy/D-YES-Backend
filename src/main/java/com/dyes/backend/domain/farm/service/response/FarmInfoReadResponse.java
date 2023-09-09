package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.farm.service.response.form.FarmInfoResponseForm;
import com.dyes.backend.domain.farm.service.response.form.FarmOperationInfoResponseForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoReadResponse {
    private FarmInfoResponseForm farmInfoResponseForm;
    private FarmOperationInfoResponseForm farmOperationInfoResponseForm;
}
