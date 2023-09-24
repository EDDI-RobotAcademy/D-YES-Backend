package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, String> {
    List<OrderedProduct> findAllByProductOrder(ProductOrder order);

    Optional<OrderedProduct> findByProductId(Long productId);
}
