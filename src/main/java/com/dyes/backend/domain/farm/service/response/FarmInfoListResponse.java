package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoListResponse {
    private Long farmId;
    private String farmName;
    private Address farmAddress;
}
