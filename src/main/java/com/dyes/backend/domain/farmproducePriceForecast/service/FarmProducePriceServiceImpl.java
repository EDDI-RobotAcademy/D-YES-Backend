package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.AnalysisRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.repository.*;
import com.dyes.backend.domain.farmproducePriceForecast.service.request.FarmProducePriceForecastData;
import com.dyes.backend.domain.farmproducePriceForecast.service.response.FarmProducePriceForecastResponseForm;
import com.dyes.backend.utility.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@ToString
@RequiredArgsConstructor
public class FarmProducePriceServiceImpl implements FarmProducePriceService {
    final private CabbagePriceRepository cabbagePriceRepository;
    final private CarrotPriceRepository carrotPriceRepository;
    final private CucumberPriceRepository cucumberPriceRepository;
    final private KimchiCabbagePriceRepository kimchiCabbagePriceRepository;
    final private OnionPriceRepository onionPriceRepository;
    final private PotatoPriceRepository potatoPriceRepository;
    final private WelshOnionPriceRepository welshOnionPriceRepository;
    final private YoungPumpkinPriceRepository youngPumpkinPriceRepository;
    final private RedisService redisService;
    @Value("${fastapi.url}")
    private String fastapi_url;

    // 양배추 예측 가격 받기
    @Override
    public void saveCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("cabbage")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("cabbage", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not cabbage");
        }
    }

    // 당근 예측 가격 받기
    @Override
    public void saveCarrotPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("carrot")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("carrot", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not carrot");
        }
    }

    // 오이 예측 가격 받기
    @Override
    public void saveCucumberPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("cucumber")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("cucumber", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not cucumber");
        }
    }

    // 배추 예측 가격 받기
    @Override
    public void saveKimchiCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("kimchiCabbage")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("kimchiCabbage", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not kimchiCabbage");
        }
    }

    // 양파 예측 가격 받기
    @Override
    public void saveOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("onion")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("onion", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not onion");
        }
    }

    // 감자 예측 가격 받기
    @Override
    public void savePotatoPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("potato")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("potato", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not potato");
        }
    }

    // 대파 예측 가격 받기
    @Override
    public void saveWelshOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("welshOnion")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("welshOnion", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not welshOnion");
        }
    }

    // 애호박 예측 가격 받기
    @Override
    public void saveYoungPumpkinPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if (farmProduceName.equals("youngPumpkin")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for (int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i + 1));
            }
            Map<String, Integer> priceListByDay = new HashMap<>();
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);
                priceListByDay.put(formattedDate, price);
            }
            FarmProducePriceForecastData farmProducePriceForecastData = new FarmProducePriceForecastData(priceListByDay);
            try {
                redisService.setFarmProducePrice("youngPumpkin", farmProducePriceForecastData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("It's not youngPumpkin");
        }
    }

    // 예측된 농산물 가격 확인
    @Override
    public List<FarmProducePriceForecastResponseForm> getPrice(LocalDate currentDate) {

        List<FarmProducePriceForecastResponseForm> farmProducePriceForecastResponseFormList = new ArrayList<>();

        // 양배추
        List<Map<LocalDate, Integer>> cabbagePriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm cabbageResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("cabbage");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();
            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                cabbagePriceList.add(priceByDay);

                cabbageResponseForm
                        = new FarmProducePriceForecastResponseForm("cabbage", cabbagePriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(cabbageResponseForm);

        // 당근
        List<Map<LocalDate, Integer>> carrotPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm carrotResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("carrot");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                carrotPriceList.add(priceByDay);

                carrotResponseForm
                        = new FarmProducePriceForecastResponseForm("carrot", carrotPriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(carrotResponseForm);

        // 오이
        List<Map<LocalDate, Integer>> cucumberPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm cucumberResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("cucumber");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                cucumberPriceList.add(priceByDay);

                cucumberResponseForm
                        = new FarmProducePriceForecastResponseForm("cucumber", cucumberPriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(cucumberResponseForm);

        // 김치
        List<Map<LocalDate, Integer>> kimchiCabbagePriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm kimchiCabbageResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("kimchiCabbage");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                kimchiCabbagePriceList.add(priceByDay);

                kimchiCabbageResponseForm
                        = new FarmProducePriceForecastResponseForm("kimchiCabbage", kimchiCabbagePriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(kimchiCabbageResponseForm);

        // 양파
        List<Map<LocalDate, Integer>> onionPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm onionResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("onion");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                onionPriceList.add(priceByDay);

                onionResponseForm
                        = new FarmProducePriceForecastResponseForm("onion", onionPriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(onionResponseForm);

        // 감자
        List<Map<LocalDate, Integer>> potatoPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm potatoResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("potato");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                potatoPriceList.add(priceByDay);

                potatoResponseForm
                        = new FarmProducePriceForecastResponseForm("potato", potatoPriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(potatoResponseForm);

        // 대파
        List<Map<LocalDate, Integer>> welshOnionPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm welshOnionResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("welshOnion");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                welshOnionPriceList.add(priceByDay);

                welshOnionResponseForm
                        = new FarmProducePriceForecastResponseForm("welshOnion", welshOnionPriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        farmProducePriceForecastResponseFormList.add(welshOnionResponseForm);

        // 애호박
        List<Map<LocalDate, Integer>> youngPumpkinPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm youngPumpkinResponseForm = new FarmProducePriceForecastResponseForm();
        try {
            FarmProducePriceForecastData farmProducePriceForecastData
                    = redisService.getFarmProducePriceForecastData("youngPumpkin");
            Map<String, Integer> priceListByDay = farmProducePriceForecastData.getPriceListByDay();

            for (int j = 0; j < 14; j++) {
                LocalDate date = currentDate.plusDays(j + 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = date.format(formatter);

                int price;
                if(priceListByDay.get(formattedDate) == null) {
                    price = 0;
                } else {
                    price = priceListByDay.get(formattedDate);
                }
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, price);
                youngPumpkinPriceList.add(priceByDay);

                youngPumpkinResponseForm
                        = new FarmProducePriceForecastResponseForm("youngPumpkin", youngPumpkinPriceList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        farmProducePriceForecastResponseFormList.add(youngPumpkinResponseForm);

        return farmProducePriceForecastResponseFormList;
    }

    @Scheduled(cron = "0 10 00 * * ?")
    public String getCabbagePriceFromFastAPI() {
        log.info("Starting cabbage price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(990, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedCabbagePrice = result_price(requestEntity);
                String numbersOnly = predictedCabbagePrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "cabbage";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveCabbagePrice(farmProducePriceRequestForm);
                return predictedCabbagePrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "15 10 00 * * ?")
    public String getGreenOnionPriceFromFastAPI() {
        log.info("Starting green onion price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(991, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedWelshOnionPrice = result_price(requestEntity);
                String numbersOnly = predictedWelshOnionPrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "welshOnion";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveWelshOnionPrice(farmProducePriceRequestForm);
                return predictedWelshOnionPrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "30 10 00 * * ?")
    public String getGreenPumpkinPriceFromFastAPI() {
        log.info("Starting green pumpkin price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(992, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedYoungPumpkinPrice = result_price(requestEntity);
                String numbersOnly = predictedYoungPumpkinPrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "youngPumpkin";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveYoungPumpkinPrice(farmProducePriceRequestForm);
                return predictedYoungPumpkinPrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "45 10 00 * * ?")
    public String getKimchiCabbagePriceFromFastAPI() {
        log.info("Starting kimchi cabbage price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(993, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedKimchiCabbagePrice = result_price(requestEntity);
                String numbersOnly = predictedKimchiCabbagePrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "kimchiCabbage";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveKimchiCabbagePrice(farmProducePriceRequestForm);
                return predictedKimchiCabbagePrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "0 11 00 * * ?")
    public String getOnionPriceFromFastAPI() {
        log.info("Starting onion price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(994, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedOnionPrice = result_price(requestEntity);
                String numbersOnly = predictedOnionPrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "onion";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveOnionPrice(farmProducePriceRequestForm);
                return predictedOnionPrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "15 11 00 * * ?")
    public String getCarrotPriceFromFastAPI() {
        log.info("Starting carrot price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(995, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedCarrotPrice = result_price(requestEntity);
                String numbersOnly = predictedCarrotPrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "carrot";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveCarrotPrice(farmProducePriceRequestForm);
                return predictedCarrotPrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "30 11 00 * * ?")
    public String getPotatoPriceFromFastAPI() {
        log.info("Starting potato price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(996, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedPotatoPrice = result_price(requestEntity);
                String numbersOnly = predictedPotatoPrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "potato";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                savePotatoPrice(farmProducePriceRequestForm);
                return predictedPotatoPrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    @Scheduled(cron = "0 12 00 * * ?")
    public String getCucumberPriceFromFastAPI() {
        log.info("Starting cucumber price prediction request...");
        String url = "http://" + fastapi_url + "/ai-request-command";

        RestTemplate restTemplate = new RestTemplate();

        AnalysisRequestForm requestForm = new AnalysisRequestForm(997, "," + "request_predict");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalysisRequestForm> requestEntity = new HttpEntity<>(requestForm, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String result = response.getBody();
            if (Objects.equals(result, "true")) {
                log.info(result);
                List<Integer> farmProducePriceList = new ArrayList<>();

                String predictedCucumberPrice = result_price(requestEntity);
                String numbersOnly = predictedCucumberPrice.replaceAll("\\[|\\]|,", "");

                log.info("정리된 값: " + numbersOnly);
                String[] numberStrings = numbersOnly.split("\\s+");

                for (String numberString : numberStrings) {
                    String cleanedNumberString = numberString.replace("\"", "");
                    log.info("정리된 스트링 숫자 값: " + cleanedNumberString);
                    if (!cleanedNumberString.isEmpty()) {
                        Integer number = Integer.valueOf(cleanedNumberString.trim());
                        log.info("정리된 정수 숫자 값: " + number);
                        farmProducePriceList.add(number);
                    }
                }

                LocalDate currentDate = LocalDate.now();
                log.info("오늘 날짜: " + currentDate);
                String farmProduceName = "cucumber";
                FarmProducePriceRequestForm farmProducePriceRequestForm
                        = new FarmProducePriceRequestForm(currentDate, farmProduceName, farmProducePriceList);
                saveCucumberPrice(farmProducePriceRequestForm);
                return predictedCucumberPrice;

            } else {
                return "농산물 가격 예측이 불가합니다.";
            }
        } else {
            return "요청 실패";
        }
    }

    public String result_price(HttpEntity<AnalysisRequestForm> requestFormHttpEntity) {
        String result_url = "http://" + fastapi_url + "/ai-response";

        RestTemplate restTemplate = new RestTemplate();

        try {
            Thread.sleep(3000); // 8초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //ai-response
        ResponseEntity<String> result_response = restTemplate.exchange(result_url, HttpMethod.GET, requestFormHttpEntity,
                String.class);
        if (result_response.getStatusCode().is2xxSuccessful()) {
            log.info("예측된 결과값: " + result_response.getBody());
            return result_response.getBody();
        } else {
            return "요청 실패";
        }
    }
}
