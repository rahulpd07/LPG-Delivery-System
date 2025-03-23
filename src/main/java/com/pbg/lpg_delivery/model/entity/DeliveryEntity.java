package com.pbg.lpg_delivery.model.entity;

import com.pbg.lpg_delivery.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "deliveries")
public class DeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_person_id", nullable = false)
    private UserEntity deliveryPerson; // Role: DELIVERY_PERSON

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // Pending, In-Transit, Delivered

    private LocalDateTime deliveryDate;

    private LocalDateTime expectedDeliveryDate;

    private String notes;

    public static DeliveryEntity createDelivery(OrderEntity order, UserEntity deliveryPerson) {
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrder(order);
        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setStatus(DeliveryStatus.IN_TRANSIT);
        delivery.setExpectedDeliveryDate(calculateExpectedDeliveryDate(LocalDateTime.now()));
        delivery.setNotes("Delivery in progress");
        return delivery;
    }

    /**
     * Calculates the expected delivery date: 24 hours after assignment, skipping Sundays.
     */
    private static LocalDateTime calculateExpectedDeliveryDate(LocalDateTime assignmentTime) {
        LocalDateTime expected = assignmentTime.plusHours(24);

        if (expected.getDayOfWeek() == DayOfWeek.SUNDAY) {
            expected = expected.plusDays(1);
        }

        return expected.withNano(0);
    }

}



