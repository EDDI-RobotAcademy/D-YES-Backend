package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.ProductManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
}
