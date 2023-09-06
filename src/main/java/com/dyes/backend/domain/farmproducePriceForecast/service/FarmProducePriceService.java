package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;

public interface FarmProducePriceService {
    void saveCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm);
}
