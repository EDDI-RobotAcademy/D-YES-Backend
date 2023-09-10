package com.dyes.backend.domain.farm.service.response.form;

import com.dyes.backend.domain.farm.service.response.FarmInfoResponseForAdmin;
import com.dyes.backend.domain.farm.service.response.FarmOperationInfoResponseForAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoReadResponseForm {
    private FarmInfoResponseForAdmin farmInfoResponseForm;
    private FarmOperationInfoResponseForAdmin farmOperationInfoResponseForm;
}
