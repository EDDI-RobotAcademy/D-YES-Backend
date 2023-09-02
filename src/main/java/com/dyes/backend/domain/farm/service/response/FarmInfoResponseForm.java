package com.dyes.backend.domain.farm.service.response;

import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoResponseForm {
    private Long farmId;
    private String farmName;
    private String csContactNumber;
    private Address farmAddress;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;
}
