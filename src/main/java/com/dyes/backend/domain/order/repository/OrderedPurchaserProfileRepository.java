package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.OrderedPurchaserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedPurchaserProfileRepository extends JpaRepository<OrderedPurchaserProfile, Long> {
}
