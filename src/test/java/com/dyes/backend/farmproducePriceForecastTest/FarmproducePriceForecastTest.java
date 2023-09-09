package com.dyes.backend.farmproducePriceForecastTest;

import com.dyes.backend.domain.admin.entity.Admin;
import com.dyes.backend.domain.farm.controller.form.FarmRegisterRequestForm;
import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.farm.service.FarmServiceImpl;
import com.dyes.backend.domain.farmproducePriceForecast.controller.form.FarmProducePriceRequestForm;
import com.dyes.backend.domain.farmproducePriceForecast.entity.CabbagePrice;
import com.dyes.backend.domain.farmproducePriceForecast.repository.*;
import com.dyes.backend.domain.farmproducePriceForecast.service.FarmProducePriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FarmproducePriceForecastTest {

    @Mock
    private CabbagePriceRepository cabbagePriceRepository;
    @Mock
    private CarrotPriceRepository carrotPriceRepository;
    @Mock
    private CucumberPriceRepository cucumberPriceRepository;
    @Mock
    private KimchiCabbagePriceRepository kimchiCabbagePriceRepository;
    @Mock
    private OnionPriceRepository onionPriceRepository;
    @Mock
    private PotatoPriceRepository potatoPriceRepository;
    @Mock
    private WelshOnionPriceRepository welshOnionPriceRepository;
    @Mock
    private YoungPumpkinPriceRepository youngPumpkinPriceRepository;
    @Mock
    private FarmProducePriceServiceImpl farmProducePriceService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        farmProducePriceService = new FarmProducePriceServiceImpl(
                cabbagePriceRepository,
                carrotPriceRepository,
                cucumberPriceRepository,
                kimchiCabbagePriceRepository,
                onionPriceRepository,
                potatoPriceRepository,
                welshOnionPriceRepository,
                youngPumpkinPriceRepository);
    }

    @Test
    @DisplayName("farmProduce mocking test: saveCabbagePrice")
    public void 예측된_양배추_가격을_저장합니다 () {
        List<Integer> farmProducePrice = new ArrayList<>();
        farmProducePrice.add((13000));
        farmProducePrice.add((12500));
        farmProducePrice.add((13400));
        farmProducePrice.add((12700));
        farmProducePrice.add((13000));
        farmProducePrice.add((13100));
        FarmProducePriceRequestForm farmProducePriceRequestForm
                = new FarmProducePriceRequestForm(LocalDate.now(), "cabbage", farmProducePrice);
        when(cabbagePriceRepository.findByDate("2023-09-09")).thenReturn(Optional.of(new CabbagePrice()));

        farmProducePriceService.saveCabbagePrice(farmProducePriceRequestForm);

        verify(cabbagePriceRepository, times(6)).findByDate(any());
        verify(cabbagePriceRepository, times(6)).save(any());
    }
}
