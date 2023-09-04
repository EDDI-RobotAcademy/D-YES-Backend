package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<ProductOrder, Long> {
}
