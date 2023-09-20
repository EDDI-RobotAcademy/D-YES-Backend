package com.dyes.backend.domain.payment.entity;

import com.dyes.backend.domain.order.entity.ProductOrder;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private String aid;
    private String tid;
    private String cid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    @Embedded
    private PaymentAmount amount;
    @Embedded
    private PaymentCardInfo card_info;
    private String item_name;
    private int quantity;
    private LocalDate created_at;
    private LocalDate approved_at;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;
}
