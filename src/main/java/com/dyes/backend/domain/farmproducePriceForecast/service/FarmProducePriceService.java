package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;

public interface FarmProducePriceService {
    void saveCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveCarrotPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveCucumberPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveKimchiCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void saveOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
    void savePotatoPrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
}
