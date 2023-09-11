package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<ProductOrder, Long> {
    @Query("select po FROM ProductOrder po join fetch po.user")
    List<ProductOrder> findAllWithUser();
}
