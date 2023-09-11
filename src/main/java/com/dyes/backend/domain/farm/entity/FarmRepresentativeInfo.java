package com.dyes.backend.domain.farm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "farm")
public class FarmRepresentativeInfo {
    @Id
    private Long id;
    private String representativeName;
    private String representativeContactNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
