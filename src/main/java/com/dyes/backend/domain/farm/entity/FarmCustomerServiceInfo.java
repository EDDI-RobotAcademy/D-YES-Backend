package com.dyes.backend.domain.farm.entity;

import com.dyes.backend.domain.user.entity.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmCustomerServiceInfo {
    @Id
    private Long id;
    private String csContactNumber;
    @Embedded
    private Address farmAddress;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
