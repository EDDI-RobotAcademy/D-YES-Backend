package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.CucumberPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CucumberPriceRepository extends JpaRepository<CucumberPrice, Long> {
    Optional<CucumberPrice> findByDate(String saveDate);
}
