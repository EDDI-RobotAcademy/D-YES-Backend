package com.dyes.backend.domain.farmproducePriceForecast.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmProducePriceRequestForm {
    private LocalDate date;
    private String farmProduceName;
    private List<Integer> farmProducePrice = new ArrayList<>();
}
