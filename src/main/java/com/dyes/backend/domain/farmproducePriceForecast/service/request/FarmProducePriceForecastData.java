package com.dyes.backend.domain.farmproducePriceForecast.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmProducePriceForecastData {
    Map<String, Integer> priceListByDay = new HashMap<>();
}
