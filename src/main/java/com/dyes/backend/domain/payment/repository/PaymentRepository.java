package com.dyes.backend.domain.payment.repository;

import com.dyes.backend.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
