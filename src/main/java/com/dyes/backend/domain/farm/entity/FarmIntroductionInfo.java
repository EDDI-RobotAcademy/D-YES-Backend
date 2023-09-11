package com.dyes.backend.domain.farm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "farm")
public class FarmIntroductionInfo {
    @Id
    private Long id;
    private String mainImage;
    private String introduction;
    private List<ProduceType> produceTypes;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
