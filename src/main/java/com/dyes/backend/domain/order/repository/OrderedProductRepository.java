package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.OrderedProduct;
import com.dyes.backend.domain.order.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, String> {
    List<OrderedProduct> findAllByProductOrder(ProductOrder order);
}
