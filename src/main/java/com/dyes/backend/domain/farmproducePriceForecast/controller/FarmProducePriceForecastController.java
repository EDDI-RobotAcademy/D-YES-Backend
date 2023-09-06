package com.dyes.backend.domain.farmproducePriceForecast.controller;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.service.FarmProducePriceService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/farmProduce")
public class FarmProducePriceForecastController {
    final private FarmProducePriceService farmProducePriceService;

    // 양배추 예측 가격 받기
    @PostMapping("/save-cabbage-price")
    public void saveCabbagePrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveCabbagePrice(farmProducePriceRequestForm);
    }

    // 당근 예측 가격 받기
    @PostMapping("/save-carrot-price")
    public void saveCarrotPrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveCarrotPrice(farmProducePriceRequestForm);
    }

    // 오이 예측 가격 받기
    @PostMapping("/save-cucumber-price")
    public void saveCucumberPrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveCucumberPrice(farmProducePriceRequestForm);
    }

    // 배추 예측 가격 받기
    @PostMapping("/save-kimchi-cabbage-price")
    public void saveKimchiCabbagePrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveKimchiCabbagePrice(farmProducePriceRequestForm);
    }
}
