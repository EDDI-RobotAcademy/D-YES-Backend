package com.dyes.backend.domain.farmproducePriceForecast.entity;

import com.dyes.backend.domain.farm.entity.ProduceType;
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
public class OnionPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private int price;
    @Enumerated(EnumType.STRING)
    private ProduceType produceType;
}
