package com.dyes.backend.domain.farm.repository;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.FarmRepresentativeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepresentativeInfoRepository extends JpaRepository<FarmRepresentativeInfo, Long> {
    FarmRepresentativeInfo findByFarm(Farm farm);
}
