package com.dyes.backend.domain.farm.service.request;

import com.dyes.backend.domain.farm.entity.ProduceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmRegisterRequest {
    private String farmName;
    private String CSContactNumber;
    private String address;
    private String zipCode;
    private String addressDetail;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;
}
