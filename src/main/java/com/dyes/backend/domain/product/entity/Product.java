package com.dyes.backend.domain.product.entity;

import com.dyes.backend.domain.farm.entity.Farm;
import com.dyes.backend.domain.farm.entity.ProduceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "farm")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String productDescription;
    @Enumerated(EnumType.STRING)
    private CultivationMethod cultivationMethod;
    @Enumerated(EnumType.STRING)
    private ProduceType produceType;
    @Enumerated(EnumType.STRING)
    private SaleStatus productSaleStatus;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id")
    private Farm farm;
    @Enumerated(EnumType.STRING)
    private MaybeEventProduct maybeEventProduct;
}
