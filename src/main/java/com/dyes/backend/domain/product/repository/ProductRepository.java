package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p FROM Product p join fetch p.farm where p.id = :productId")
    Product findByIdWithFarm(Long productId);
}
