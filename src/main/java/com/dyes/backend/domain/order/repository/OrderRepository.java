package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<ProductOrder, Long> {
    @Query("select po FROM ProductOrder po join fetch po.user")
    List<ProductOrder> findAllWithUser();

    @Query("select po FROM ProductOrder po join fetch po.user u where u = :user")
    List<ProductOrder> findAllByUserWithUser(User user);
}
