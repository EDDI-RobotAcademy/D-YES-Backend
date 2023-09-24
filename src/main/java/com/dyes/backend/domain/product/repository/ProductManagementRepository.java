package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
    @Query("select pm FROM ProductManagement pm join fetch pm.product p join fetch p.farm where pm.createdDate between :startDate and :endDate")
    Page<ProductManagement> findByCreatedDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("select pm FROM ProductManagement pm join fetch pm.product p join fetch p.farm where pm.createdDate > :startDate ORDER BY pm.createdDate DESC")
    List<ProductManagement> findAllByCreatedDateAfterOrderByCreatedDateDesc(@Param("startDate") LocalDate startDate);

    Optional<ProductManagement> findByProduct(Product product);
}
