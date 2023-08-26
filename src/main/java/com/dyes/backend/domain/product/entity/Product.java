package com.dyes.backend.domain.product.entity;

import com.dyes.backend.domain.farm.entity.Farm;
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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String productDescription;
    @Enumerated(EnumType.STRING)
    private CultivationMethod cultivationMethod;
    @Enumerated(EnumType.STRING)
    private SaleStatus productSaleStatus;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
