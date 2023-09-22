package com.dyes.backend.domain.event.service.request.register;

import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.product.entity.CultivationMethod;
import com.dyes.backend.domain.product.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProductRegisterRequest {
    private String userToken;
    private String productName;
    private String productDescription;
    private CultivationMethod cultivationMethod;
    private ProduceType produceType;
    private String optionName;
    private Long optionPrice;
    private int stock;
    private Long value;
    private Unit unit;
    private String mainImg;
    private List<String> detailImgs;
    private String farmName;
}
