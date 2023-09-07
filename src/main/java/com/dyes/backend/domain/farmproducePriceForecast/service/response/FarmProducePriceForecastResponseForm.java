package com.dyes.backend.domain.farmproducePriceForecast.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmProducePriceForecastResponseForm {
    private String farmProduceName;
    private List<Map<LocalDate, Integer>> priceList = new ArrayList<>();
}
