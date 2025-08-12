package com.pbg.lpg_delivery.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dealer_address")
@Getter
@Setter
public class DealerAddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pincode;
    private String street;
    private String city;
    private String state;

    @OneToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private DealerEntity dealer;
}
