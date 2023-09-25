package com.dyes.backend.domain.event.repository;

import com.dyes.backend.domain.event.entity.EventProduct;
import com.dyes.backend.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventProductRepository extends JpaRepository<EventProduct, Long> {
    @Query("SELECT ep FROM EventProduct ep " +
            "JOIN FETCH ep.productOption po " +
            "JOIN FETCH po.product p " +
            "JOIN FETCH p.farm f " +
            "JOIN FETCH ep.eventPurchaseCount epc " +
            "JOIN FETCH ep.eventDeadLine ed")
    List<EventProduct> findAllWithProductOptionDeadLineCount();

    @Query("SELECT ep FROM EventProduct ep " +
            "JOIN FETCH ep.productOption po " +
            "JOIN FETCH po.product p " +
            "JOIN FETCH p.farm f " +
            "JOIN FETCH ep.eventPurchaseCount epc " +
            "JOIN FETCH ep.eventDeadLine ed " +
            "WHERE ep.id = :eventProductId")
    Optional<EventProduct> findByIdProductOptionDeadLineCount(@Param("eventProductId") Long eventProductId);

    @Query("SELECT e FROM EventProduct e JOIN FETCH e.eventPurchaseCount WHERE e.productOption = :productOption")
    Optional<EventProduct> findByProductOptionWithPurchaseCount(@Param("productOption") ProductOption productOption);
}

