package com.dyes.backend.domain.payment.repository;

import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.tid = :tid")
    Optional<Payment> findByTid(@Param("tid") String tid);
    Optional<Payment> findByProductOrder(ProductOrder order);
}
