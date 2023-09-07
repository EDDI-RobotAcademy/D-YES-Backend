package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.CarrotPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrotPriceRepository extends JpaRepository<CarrotPrice, Long> {
    Optional<CarrotPrice> findByDate(String saveDate);
}
