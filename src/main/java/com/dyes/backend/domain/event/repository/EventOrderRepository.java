package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventOrderRepository extends JpaRepository<EventOrder, Long> {
}
