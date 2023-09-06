package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.PotatoPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PotatoPriceRepository extends JpaRepository<PotatoPrice, Long> {
    Optional<PotatoPrice> findByDate(String saveDate);
}
