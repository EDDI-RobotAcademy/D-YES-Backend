package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.OrderedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, String> {
}
