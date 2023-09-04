package com.dyes.backend.domain.cart.repository;

import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import com.dyes.backend.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContainProductOptionRepository extends JpaRepository<ContainProductOption, Long> {
    @Query("select cpo FROM ContainProductOption cpo join fetch cpo.cart where cpo.cart = :cart")
    List<ContainProductOption> findAllByCart (@Param("cart") Cart cart);

    @Query("select cpo FROM ContainProductOption cpo join fetch cpo.cart cart join fetch cpo.productOption po join fetch po.product where cart = :cart")
    List<ContainProductOption> findAllByCartWithProduct(@Param("cart") Cart cart);

    @Query("select cpo FROM ContainProductOption cpo join fetch cpo.productOption where cpo.productOption = :productOption")
    List<ContainProductOption> findAllByProductOption(ProductOption productOption);
}
