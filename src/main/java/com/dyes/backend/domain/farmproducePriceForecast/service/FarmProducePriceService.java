package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.service.response.FarmProducePriceForecastResponseForm;

import java.time.LocalDate;
import java.util.List;

public interface FarmProducePriceService {
    void saveCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveCarrotPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveCucumberPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveKimchiCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void savePotatoPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveWelshOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveYoungPumpkinPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    List<FarmProducePriceForecastResponseForm> getPrice(LocalDate currentDate);
}
