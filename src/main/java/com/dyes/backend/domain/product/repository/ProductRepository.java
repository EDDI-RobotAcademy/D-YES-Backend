package com.dyes.backend.domain.product.repository;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.ProduceType;
import com.dyes.backend.domain.product.entity.CultivationMethod;
import com.dyes.backend.domain.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByFarm(Farm farm);

    @Query("select p FROM Product p join fetch p.farm where p.id = :productId")
    Optional<Product> findByIdWithFarm(Long productId);

    @Query("select p FROM Product p join fetch p.farm where p.id in :productIdList")
    List<Product> findAllByIdWithFarm(@Param("productId") List<Long> productIdList);

    @Query("select p FROM Product p join fetch p.farm")
    List<Product> findAllWithFarm();

    @Query("select p FROM Product p join fetch p.farm where p.farm = :farm")
    List<Product> findAllByFarmWithFarm(Farm farm);

    @Query("select p FROM Product p join fetch p.farm where p.cultivationMethod = :categoryName")
    List<Product> findAllWithFarmByCategory(@Param("categoryName") CultivationMethod categoryName);

    Optional<Product> findByProduceType(ProduceType produceType);

    List<Product> findAllByProduceType(ProduceType produceType);
}
