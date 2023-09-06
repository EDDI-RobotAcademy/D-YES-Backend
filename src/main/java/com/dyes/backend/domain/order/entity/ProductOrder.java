package com.dyes.backend.domain.order.entity;

import com.dyes.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder {
    @Id
    private String id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
    private int totalAmount;
    private LocalDate orderedTime;
}
