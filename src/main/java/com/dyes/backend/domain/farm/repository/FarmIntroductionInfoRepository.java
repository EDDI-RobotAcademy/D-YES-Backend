package com.dyes.backend.domain.farm.repository;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmIntroductionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmIntroductionInfoRepository extends JpaRepository<FarmIntroductionInfo, Long> {
    FarmIntroductionInfo findByFarm(Farm farm);
}
