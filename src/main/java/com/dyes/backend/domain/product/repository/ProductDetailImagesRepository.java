package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductDetailImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDetailImagesRepository extends JpaRepository<ProductDetailImages, Long> {
    @Query("select pd FROM ProductDetailImages pd join fetch pd.product p where p = :product")
    List<ProductDetailImages> findByProduct(Product product);
}
