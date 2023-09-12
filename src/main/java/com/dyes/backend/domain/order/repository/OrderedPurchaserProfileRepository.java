package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.OrderedPurchaserProfile;
import com.dyes.backend.domain.order.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderedPurchaserProfileRepository extends JpaRepository<OrderedPurchaserProfile, Long> {
    Optional<OrderedPurchaserProfile> findByProductOrder(ProductOrder order);
}
