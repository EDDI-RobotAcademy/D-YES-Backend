package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.OnionPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnionPriceRepository extends JpaRepository<OnionPrice, Long> {
    Optional<OnionPrice> findByDate(String saveDate);
}
