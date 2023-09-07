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
public class OrderedPurchaserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderedPurchaseName;
    private String orderedPurchaseContactNumber;
    private String orderedPurchaseEmail;
    @Embedded
    private OrderedPurchaserProfileAddress orderedPurchaseProfileAddress;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductOrder productOrder;
}
