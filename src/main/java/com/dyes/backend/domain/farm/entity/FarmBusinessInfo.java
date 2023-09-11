package com.dyes.backend.domain.farm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "farm")
public class FarmBusinessInfo {
    @Id
    private Long id;
    private String businessName;
    private String businessNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
