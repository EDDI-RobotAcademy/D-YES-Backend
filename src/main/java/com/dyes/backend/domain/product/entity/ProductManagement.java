package com.dyes.backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "product")
public class ProductManagement {
    @Id
    private Long id;
    private LocalDate createdDate;
    private Long adminId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
}
