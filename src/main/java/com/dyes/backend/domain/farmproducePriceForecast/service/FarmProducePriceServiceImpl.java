package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.AnalysisRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.entity.*;
import com.dyes.backend.domain.farmproducePriceForecast.repository.*;
import com.dyes.backend.domain.farmproducePriceForecast.service.response.FarmProducePriceForecastResponseForm;
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

import static com.dyes.backend.domain.farm.entity.ProduceType.*;

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
    @Value("${fastapi.url}")
    private String fastapi_url;

    // 양배추 예측 가격 받기
    @Override
    public void saveCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("cabbage")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<CabbagePrice> maybeCabbagePrice = cabbagePriceRepository.findByDate(formattedDate);

                CabbagePrice cabbagePrice;
                if(maybeCabbagePrice.isPresent()) {
                    cabbagePrice = maybeCabbagePrice.get();
                    cabbagePrice.setPrice(price);
                } else {
                    cabbagePrice = CabbagePrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(CABBAGE)
                            .build();
                }
                cabbagePriceRepository.save(cabbagePrice);
            }
        } else {
            log.info("It's not cabbage");
        }
    }

    // 당근 예측 가격 받기
    @Override
    public void saveCarrotPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("carrot")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<CarrotPrice> maybeCarrotPrice = carrotPriceRepository.findByDate(formattedDate);

                CarrotPrice carrotPrice;
                if(maybeCarrotPrice.isPresent()) {
                    carrotPrice = maybeCarrotPrice.get();
                    carrotPrice.setPrice(price);
                } else {
                    carrotPrice = CarrotPrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(CARROT)
                            .build();
                }
                carrotPriceRepository.save(carrotPrice);
            }
        } else {
            log.info("It's not carrot");
        }
    }

    // 오이 예측 가격 받기
    @Override
    public void saveCucumberPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("cucumber")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<CucumberPrice> maybeCucumberPrice = cucumberPriceRepository.findByDate(formattedDate);

                CucumberPrice cucumberPrice;
                if(maybeCucumberPrice.isPresent()) {
                    cucumberPrice = maybeCucumberPrice.get();
                    cucumberPrice.setPrice(price);
                } else {
                    cucumberPrice = CucumberPrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(CUCUMBER)
                            .build();
                }
                cucumberPriceRepository.save(cucumberPrice);
            }
        } else {
            log.info("It's not cucumber");
        }
    }

    // 배추 예측 가격 받기
    @Override
    public void saveKimchiCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("kimchiCabbage")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<KimchiCabbagePrice> maybeKimchiCabbagePrice = kimchiCabbagePriceRepository.findByDate(formattedDate);

                KimchiCabbagePrice kimchiCabbagePrice;
                if(maybeKimchiCabbagePrice.isPresent()) {
                    kimchiCabbagePrice = maybeKimchiCabbagePrice.get();
                    kimchiCabbagePrice.setPrice(price);
                } else {
                    kimchiCabbagePrice = KimchiCabbagePrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(KIMCHI_CABBAGE)
                            .build();
                }
                kimchiCabbagePriceRepository.save(kimchiCabbagePrice);
            }
        } else {
            log.info("It's not kimchiCabbage");
        }
    }

    // 양파 예측 가격 받기
    @Override
    public void saveOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("onion")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<OnionPrice> maybeOnionPrice = onionPriceRepository.findByDate(formattedDate);

                OnionPrice onionPrice;
                if(maybeOnionPrice.isPresent()) {
                    onionPrice = maybeOnionPrice.get();
                    onionPrice.setPrice(price);
                } else {
                    onionPrice = OnionPrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(ONION)
                            .build();
                }
                onionPriceRepository.save(onionPrice);
            }
        } else {
            log.info("It's not onion");
        }
    }

    // 감자 예측 가격 받기
    @Override
    public void savePotatoPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("potato")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<PotatoPrice> maybePotatoPrice = potatoPriceRepository.findByDate(formattedDate);

                PotatoPrice potatoPrice;
                if(maybePotatoPrice.isPresent()) {
                    potatoPrice = maybePotatoPrice.get();
                    potatoPrice.setPrice(price);
                } else {
                    potatoPrice = PotatoPrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(POTATO)
                            .build();
                }
                potatoPriceRepository.save(potatoPrice);
            }
        } else {
            log.info("It's not potato");
        }
    }

    // 대파 예측 가격 받기
    @Override
    public void saveWelshOnionPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("welshOnion")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<WelshOnionPrice> maybeWelshOnionPrice = welshOnionPriceRepository.findByDate(formattedDate);

                WelshOnionPrice welshOnionPrice;
                if(maybeWelshOnionPrice.isPresent()) {
                    welshOnionPrice = maybeWelshOnionPrice.get();
                    welshOnionPrice.setPrice(price);
                } else {
                    welshOnionPrice = WelshOnionPrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(WELSH_ONION)
                            .build();
                }
                welshOnionPriceRepository.save(welshOnionPrice);
            }
        } else {
            log.info("It's not welshOnion");
        }
    }

    // 애호박 예측 가격 받기
    @Override
    public void saveYoungPumpkinPrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("youngPumpkin")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i+1));
            }

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate saveDate = dateList.get(i);
                Integer price = priceList.get(i);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = saveDate.format(formatter);

                Optional<YoungPumpkinPrice> maybeYoungPumpkinPrice = youngPumpkinPriceRepository.findByDate(formattedDate);

                YoungPumpkinPrice youngPumpkinPrice;
                if(maybeYoungPumpkinPrice.isPresent()) {
                    youngPumpkinPrice = maybeYoungPumpkinPrice.get();
                    youngPumpkinPrice.setPrice(price);
                } else {
                    youngPumpkinPrice = YoungPumpkinPrice.builder()
                            .date(formattedDate)
                            .price(price)
                            .produceType(YOUNG_PUMPKIN)
                            .build();
                }
                youngPumpkinPriceRepository.save(youngPumpkinPrice);
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
        List<Map<LocalDate, Integer>> CabbagePriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm cabbageResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<CabbagePrice> maybeCabbagePrice = cabbagePriceRepository.findByDate(formattedDate);

            if(maybeCabbagePrice.isEmpty()) {
                log.info("Cabbage price is empty");
            } else {
                CabbagePrice cabbagePrice = maybeCabbagePrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, cabbagePrice.getPrice());
                CabbagePriceList.add(priceByDay);
            }
            cabbageResponseForm
                    = new FarmProducePriceForecastResponseForm("cabbage", CabbagePriceList);
            }
        farmProducePriceForecastResponseFormList.add(cabbageResponseForm);

        // 당근
        List<Map<LocalDate, Integer>> CarrotPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm carrotResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<CarrotPrice> maybeCarrotPrice = carrotPriceRepository.findByDate(formattedDate);

            if(maybeCarrotPrice.isEmpty()) {
                log.info("Carrot price is empty");
            } else {
                CarrotPrice carrotPrice = maybeCarrotPrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, carrotPrice.getPrice());
                CarrotPriceList.add(priceByDay);
            }
            carrotResponseForm
                    = new FarmProducePriceForecastResponseForm("carrot", CarrotPriceList);
            }
        farmProducePriceForecastResponseFormList.add(carrotResponseForm);
        
        // 오이
        List<Map<LocalDate, Integer>> cucumberPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm cucumberResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<CucumberPrice> maybeCucumberPrice = cucumberPriceRepository.findByDate(formattedDate);

            if(maybeCucumberPrice.isEmpty()) {
                log.info("Cucumber price is empty");
            } else {
                CucumberPrice cucumberPrice = maybeCucumberPrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, cucumberPrice.getPrice());
                cucumberPriceList.add(priceByDay);
            }
            cucumberResponseForm
                    = new FarmProducePriceForecastResponseForm("cucumber", cucumberPriceList);
            }
        farmProducePriceForecastResponseFormList.add(cucumberResponseForm);

        // 김치
        List<Map<LocalDate, Integer>> kimchiCabbagePriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm kimchiCabbageResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<KimchiCabbagePrice> maybeKimchiCabbagePrice = kimchiCabbagePriceRepository.findByDate(formattedDate);

            if(maybeKimchiCabbagePrice.isEmpty()) {
                log.info("KimchiCabbage price is empty");
            } else {
                KimchiCabbagePrice kimchiCabbagePrice = maybeKimchiCabbagePrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, kimchiCabbagePrice.getPrice());
                kimchiCabbagePriceList.add(priceByDay);
            }
            kimchiCabbageResponseForm
                    = new FarmProducePriceForecastResponseForm("kimchiCabbage", kimchiCabbagePriceList);
        }
        farmProducePriceForecastResponseFormList.add(kimchiCabbageResponseForm);

        // 양파
        List<Map<LocalDate, Integer>> onionPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm onionResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<OnionPrice> maybeOnionPrice = onionPriceRepository.findByDate(formattedDate);

            if(maybeOnionPrice.isEmpty()) {
                log.info("Onion price is empty");
            } else {
                OnionPrice onionPrice = maybeOnionPrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, onionPrice.getPrice());
                onionPriceList.add(priceByDay);
            }
            onionResponseForm
                    = new FarmProducePriceForecastResponseForm("onion", onionPriceList);
            }
        farmProducePriceForecastResponseFormList.add(onionResponseForm);

        // 감자
        List<Map<LocalDate, Integer>> potatoPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm potatoResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<PotatoPrice> maybePotatoPrice = potatoPriceRepository.findByDate(formattedDate);

            if(maybePotatoPrice.isEmpty()) {
                log.info("Potato price is empty");
            } else {
                PotatoPrice potatoPrice = maybePotatoPrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, potatoPrice.getPrice());
                potatoPriceList.add(priceByDay);
            }
            potatoResponseForm
                    = new FarmProducePriceForecastResponseForm("potato", potatoPriceList);
            }
        farmProducePriceForecastResponseFormList.add(potatoResponseForm);

        // 대파
        List<Map<LocalDate, Integer>> welshOnionPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm welshOnionResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<WelshOnionPrice> maybeWelshOnionPrice = welshOnionPriceRepository.findByDate(formattedDate);

            if(maybeWelshOnionPrice.isEmpty()) {
                log.info("WelshOnion price is empty");
            } else {
                WelshOnionPrice welshOnionPrice = maybeWelshOnionPrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, welshOnionPrice.getPrice());
                welshOnionPriceList.add(priceByDay);
            }
            welshOnionResponseForm
                    = new FarmProducePriceForecastResponseForm("welshOnion", welshOnionPriceList);
            }
        farmProducePriceForecastResponseFormList.add(welshOnionResponseForm);

        // 애호박
        List<Map<LocalDate, Integer>> youngPumpkinPriceList = new ArrayList<>();
        FarmProducePriceForecastResponseForm youngPumpkinResponseForm = new FarmProducePriceForecastResponseForm();
        for(int j = 0; j < 14; j++) {
            LocalDate date = currentDate.plusDays(j+1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);

            Optional<YoungPumpkinPrice> maybeYoungPumpkinPrice = youngPumpkinPriceRepository.findByDate(formattedDate);

            if(maybeYoungPumpkinPrice.isEmpty()) {
                log.info("YoungPumpkin price is empty");
            } else {
                YoungPumpkinPrice youngPumpkinPrice = maybeYoungPumpkinPrice.get();
                Map<LocalDate, Integer> priceByDay = new HashMap<>();
                priceByDay.put(date, youngPumpkinPrice.getPrice());
                youngPumpkinPriceList.add(priceByDay);
            }
            youngPumpkinResponseForm
                    = new FarmProducePriceForecastResponseForm("youngPumpkin", youngPumpkinPriceList);
            }
        farmProducePriceForecastResponseFormList.add(youngPumpkinResponseForm);

        return farmProducePriceForecastResponseFormList;
    }

    @Scheduled(cron = "0 45 14 * * ?")
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

                String predictedCabbagePrice =  result_price(requestEntity);
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

    @Scheduled(cron = "15 45 14 * * ?")
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

                String predictedWelshOnionPrice =  result_price(requestEntity);
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

    @Scheduled(cron = "30 45 14 * * ?")
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

                String predictedYoungPumpkinPrice =  result_price(requestEntity);
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

    @Scheduled(cron = "45 45 14 * * ?")
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

                String predictedKimchiCabbagePrice =  result_price(requestEntity);
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

    @Scheduled(cron = "0 46 14 * * ?")
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

                String predictedOnionPrice =  result_price(requestEntity);
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

    @Scheduled(cron = "15 46 14 * * ?")
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

                String predictedCarrotPrice =  result_price(requestEntity);
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

    @Scheduled(cron = "30 46 14 * * ?")
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

                String predictedPotatoPrice =  result_price(requestEntity);
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

    @Scheduled(cron = "0 47 14 * * ?")
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

                String predictedCucumberPrice =  result_price(requestEntity);
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
