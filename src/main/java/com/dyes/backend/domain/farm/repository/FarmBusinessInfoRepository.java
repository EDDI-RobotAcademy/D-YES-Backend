package com.dyes.backend.domain.farm.repository;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmBusinessInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmBusinessInfoRepository extends JpaRepository<FarmBusinessInfo, Long> {
    FarmBusinessInfo findByFarm(Farm farm);
}
