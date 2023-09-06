package com.dyes.backend.domain.farmproducePriceForecast.repository;

import com.dyes.backend.domain.farmproducePriceForecast.entity.KimchiCabbagePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KimchiCabbagePriceRepository extends JpaRepository<KimchiCabbagePrice, Long> {
    Optional<KimchiCabbagePrice> findByDate(String saveDate);
}
