package com.dyes.backend.domain.farmproducePriceForecast.controller;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.service.FarmProducePriceService;
import com.dyes.backend.domain.farmproducePriceForecast.service.response.FarmProducePriceForecastResponseForm;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    // 양파 예측 가격 받기
    @PostMapping("/save-onion-price")
    public void saveOnionPrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveOnionPrice(farmProducePriceRequestForm);
    }

    // 감자 예측 가격 받기
    @PostMapping("/save-potato-price")
    public void savePotatoPrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.savePotatoPrice(farmProducePriceRequestForm);
    }

    // 대파 예측 가격 받기
    @PostMapping("/save-welsh-onion-price")
    public void saveWelshOnionPrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveWelshOnionPrice(farmProducePriceRequestForm);
    }

    // 애호박 예측 가격 받기
    @PostMapping("/save-young-pumpkin-price")
    public void saveYoungPumpkinPrice (@RequestBody FarmProducePriceRequestForm farmProducePriceRequestForm) {
        log.info("Save " + farmProducePriceRequestForm.getFarmProduceName() + " price");
        farmProducePriceService.saveYoungPumpkinPrice(farmProducePriceRequestForm);
    }

    // 예측된 농산물 가격 확인
    @GetMapping("/get-price")
    public List<FarmProducePriceForecastResponseForm> getPrice (@RequestParam("currentDate") LocalDate currentDate) {

        return farmProducePriceService.getPrice(currentDate);
    }
}
