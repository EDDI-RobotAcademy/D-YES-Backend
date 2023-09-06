package com.dyes.backend.domain.farmproducePriceForecast.service;

import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.entity.CabbagePrice;
import com.dyes.backend.domain.farmproducePriceForecast.entity.CarrotPrice;
import com.dyes.backend.domain.farmproducePriceForecast.repository.CabbagePriceRepository;
import com.dyes.backend.domain.farmproducePriceForecast.repository.CarrotPriceRepository;
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
}
