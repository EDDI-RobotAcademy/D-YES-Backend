package com.dyes.backend.domain.order.entity;

import com.dyes.backend.domain.user.entity.Address;
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
    private Address orderedPurchaseProfileAddress;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;
}
