package com.dyes.backend.domain.payment.repository;

import com.dyes.backend.domain.payment.entity.RefundedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundedPaymentRepository extends JpaRepository<RefundedPayment, String> {
}
