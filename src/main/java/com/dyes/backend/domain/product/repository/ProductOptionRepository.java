package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.product.entity.Product;
import com.dyes.backend.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    @Query("select po FROM ProductOption po join fetch po.product p where p = :product")
    List<ProductOption> findByProduct(Product product);
    @Query("select po FROM ProductOption po join fetch po.product p join fetch p.farm where po.id = :id")
    Optional<ProductOption> findByIdWithProductAndFarm(@Param("id") Long id);
    @Query("select po FROM ProductOption po join fetch po.product p where po.id = :id")
    Optional<ProductOption> findByIdWithProduct(@Param("id") Long id);

}
