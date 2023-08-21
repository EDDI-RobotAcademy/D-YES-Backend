package com.dyes.backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Amount {
    private Long value;
    @Enumerated(EnumType.STRING)
    private Unit unit;
}
