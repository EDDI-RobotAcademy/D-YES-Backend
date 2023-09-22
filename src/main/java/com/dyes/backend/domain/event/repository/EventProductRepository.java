package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventProductRepository extends JpaRepository<EventProduct, Long> {
}
