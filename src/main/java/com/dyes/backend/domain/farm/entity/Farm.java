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
@NoArgsConstructor
@AllArgsConstructor
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String farmName;
    private String farmOwnerName;
    @Embedded
    private Address farmAddress;
    private String contactNumber;
}
