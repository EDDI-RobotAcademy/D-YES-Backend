package com.dyes.backend.domain.payment.entity;

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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    @Embedded
    private PaymentAmount paymentAmount;
    private String item_name;
    private int quantity;
    private String approved_at;
}
