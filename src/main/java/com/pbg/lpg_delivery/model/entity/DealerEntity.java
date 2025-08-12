package com.pbg.lpg_delivery.model.entity;

import com.pbg.lpg_delivery.model.responses.Dealer;
import com.pbg.lpg_delivery.model.responses.DealerAddress;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dealer")
@Getter
@Setter
public class DealerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String companyName;
    private boolean authorized;

    @OneToOne(mappedBy = "dealer", cascade = CascadeType.ALL)
    private DealerAddressEntity dealerAddress;

    public Dealer mapToDto(){
        return new Dealer(
                this.companyName,
                this.authorized,
                new DealerAddress(dealerAddress.getState(),dealerAddress.getCity(),dealerAddress.getStreet(),dealerAddress.getPincode())
        );
    }
}
