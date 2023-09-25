package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventOrderRepository extends JpaRepository<EventOrder, Long> {
    @Query("SELECT eo FROM EventOrder eo " +
            "JOIN FETCH eo.eventProduct ep " +
            "JOIN FETCH ep.eventDeadLine ed " +
            "JOIN FETCH ep.eventPurchaseCount epc "+
            "JOIN FETCH eo.productOrder po "+
            "JOIN FETCH po.user")
    List<EventOrder> findAllWithDeadlineAndCountAndUser();
}
