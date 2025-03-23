package com.pbg.lpg_delivery.model.entity;



import com.pbg.lpg_delivery.model.enums.PaymentMethod;
import com.pbg.lpg_delivery.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // Online, Cash on Delivery

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // Success, Failed, Pending

    private LocalDateTime paymentDate;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreated(){
        this.createdAt = LocalDateTime.now().withNano(0);
    }
}



