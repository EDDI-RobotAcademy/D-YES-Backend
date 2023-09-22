package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventPurchaseCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventPurchaseCountRepository extends JpaRepository<EventPurchaseCount, Long> {
}
