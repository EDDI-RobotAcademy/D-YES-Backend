package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.CabbagePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmProducePriceRepository extends JpaRepository<CabbagePrice, Long> {
    Optional<CabbagePrice> findByDate(String saveDate);
}
