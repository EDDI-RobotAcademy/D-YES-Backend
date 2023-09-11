package com.dyes.backend.domain.farm.service.request;

import com.dyes.backend.domain.farm.entity.ProduceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmIntroductionInfoModifyRequest {
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;
}
