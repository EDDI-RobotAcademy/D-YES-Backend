package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.ProductManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductManagementRepository extends JpaRepository<ProductManagement, Long> {
    @Query("select pm FROM ProductManagement pm join fetch pm.product p join fetch p.farm")
    Page<ProductManagement> findNew10ByCreatedDate(Pageable pageable);

    List<ProductManagement> findAllByOrderByCreatedDateAsc();

    @Query("select pm FROM ProductManagement pm join fetch pm.product p join fetch p.farm where pm.id = :productManagementId")
    List<ProductManagement> findByIdWithProductAndFarm(Long productManagementId);
}
