package com.dyes.backend.domain.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private Long productOptionId;
    private int productOptionCount;
    @Enumerated(EnumType.STRING)
    private OrderedProductStatus orderedProductStatus;
    @Column(nullable = true)
    private String refundReason;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;
}
