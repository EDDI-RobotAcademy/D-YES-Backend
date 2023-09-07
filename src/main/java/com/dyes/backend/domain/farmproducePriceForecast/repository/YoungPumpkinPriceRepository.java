package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.YoungPumpkinPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YoungPumpkinPriceRepository extends JpaRepository<YoungPumpkinPrice, Long> {
    Optional<YoungPumpkinPrice> findByDate(String saveDate);
}
