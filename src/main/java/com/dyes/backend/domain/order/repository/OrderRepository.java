package com.dyes.backend.domain.order.repository;

import com.dyes.backend.domain.order.entity.ProductOrder;
import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<ProductOrder, Long> {
    @Query("select po FROM ProductOrder po join fetch po.user join fetch po.delivery")
    List<ProductOrder> findAllWithUser();

    @Query("select po FROM ProductOrder po join fetch po.user u join fetch po.delivery where u = :user")
    List<ProductOrder> findAllByUserWithUserAndDelivery(User user);

    List<ProductOrder> findAllByUser(User user);

    @Query("select po FROM ProductOrder po join fetch po.delivery where po.id = :productOrderId")
    Optional<ProductOrder> findByStringIdWithDelivery(@Param("productOrderId") Long productOrderId);

    @Query("select po FROM ProductOrder po join fetch po.user join fetch po.delivery order by po.orderedTime ASC")
    List<ProductOrder> findAllByOrderedTimeWithUser();
}
