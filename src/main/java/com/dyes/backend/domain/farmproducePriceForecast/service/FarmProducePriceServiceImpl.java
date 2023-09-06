package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.entity.*;
import com.dyes.backend.domain.farmproducePriceForecast.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // 양배추 예측 가격 받기
    @Override
    public void saveCabbagePrice(FarmProducePriceRequestForm farmProducePriceRequestForm) {
        final String farmProduceName = farmProducePriceRequestForm.getFarmProduceName();
        if(farmProduceName.equals("cabbage")) {
            final LocalDate startDate = farmProducePriceRequestForm.getDate();
            final List<Integer> priceList = farmProducePriceRequestForm.getFarmProducePrice();

            List<LocalDate> dateList = new ArrayList<>();
            for(int i = 0; i < priceList.size(); i++) {
                dateList.add(startDate.plusDays(i));
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
                dateList.add(startDate.plusDays(i));
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
                dateList.add(startDate.plusDays(i));
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
                dateList.add(startDate.plusDays(i));
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
                dateList.add(startDate.plusDays(i));
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
                dateList.add(startDate.plusDays(i));
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
                            .build();
                }
                potatoPriceRepository.save(potatoPrice);
            }
        } else {
            log.info("It's not potato");
        }
    }
}
