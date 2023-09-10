package com.dyes.backend.domain.farm.service.response.form;

import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoListResponseForm {
    private Long farmId;
    private String farmName;
    private Address farmAddress;
}
