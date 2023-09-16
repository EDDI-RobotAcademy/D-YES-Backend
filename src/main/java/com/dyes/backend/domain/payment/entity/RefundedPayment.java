package com.dyes.backend.domain.payment.entity;

import com.dyes.backend.domain.product.entity.ProductOption;
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
public class RefundedPayment {
    @Id
    private String aid;
    private String tid;
    @OneToMany(fetch = FetchType.LAZY)
    private List<ProductOption> productOptionList;
    private int approved_cancel_amount;
    private LocalDate canceled_at;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
