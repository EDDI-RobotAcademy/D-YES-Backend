package com.dyes.backend.domain.farm.repository;

import com.dyes.backend.domain.farm.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByFarmName(String farmName);
}
