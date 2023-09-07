package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.WelshOnionPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WelshOnionPriceRepository extends JpaRepository<WelshOnionPrice, Long> {
    Optional<WelshOnionPrice> findByDate(String saveDate);
}
