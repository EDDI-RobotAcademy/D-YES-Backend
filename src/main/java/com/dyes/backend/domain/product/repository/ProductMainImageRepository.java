package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductMainImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductMainImageRepository extends JpaRepository<ProductMainImage, Long> {
    @Query("select pm FROM ProductMainImage pm join fetch pm.product p where p = :product")
    Optional<ProductMainImage> findByProduct(Product product);
}
