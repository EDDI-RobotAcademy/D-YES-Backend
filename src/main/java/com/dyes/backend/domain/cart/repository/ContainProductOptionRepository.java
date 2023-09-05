package com.dyes.backend.domain.cart.repository;

import com.dyes.backend.domain.cart.entity.Cart;
import com.dyes.backend.domain.cart.entity.ContainProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContainProductOptionRepository extends JpaRepository<ContainProductOption, Long> {
    @Query("select cpo FROM ContainProductOption cpo join fetch cpo.cart where cpo.cart = :cart")
    List<ContainProductOption> findAllByCart (@Param("cart") Cart cart);

    List<ContainProductOption> findAllByOptionId(Long id);

//    @Query("select cpo FROM ContainProductOption cpo join fetch cpo.cart cart join fetch cpo.productOption po join fetch po.product where cart = :cart")
//    List<ContainProductOption> findAllByCartWithProduct(@Param("cart") Cart cart);
}
