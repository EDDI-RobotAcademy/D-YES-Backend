package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventProductRepository extends JpaRepository<EventProduct, Long> {
    @Query("SELECT ep FROM EventProduct ep " +
            "JOIN FETCH ep.productOption po " +
            "JOIN FETCH po.product p " +
            "JOIN FETCH p.farm f " +
            "JOIN FETCH ep.eventPurchaseCount epc " +
            "JOIN FETCH ep.eventDeadLine ed")
    List<EventProduct> findAllWithProductOptionDeadLineCount();
}

