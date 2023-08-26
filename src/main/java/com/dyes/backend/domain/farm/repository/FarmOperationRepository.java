package com.dyes.backend.domain.farm.repository;

import com.dyes.backend.domain.farm.entity.FarmOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmOperationRepository extends JpaRepository<FarmOperation, Long> {
}
