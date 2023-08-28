package com.dyes.backend.domain.product.service.response.admin;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmInfoResponseForAdmin {
    private Long farmId;
    private String farmName;
    private String csContactNumber;
    private Address farmAddress;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;

    public FarmInfoResponseForAdmin farmInfoResponseForAdmin (Farm farm) {
        return new FarmInfoResponseForAdmin(
                farm.getId(),
                farm.getFarmName(), farm.getCsContactNumber(), farm.getFarmAddress(),
                farm.getMainImage(), farm.getIntroduction(), farm.getProduceTypes());
    }
}
