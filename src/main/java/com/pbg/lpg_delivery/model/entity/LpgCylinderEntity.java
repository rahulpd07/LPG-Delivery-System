package com.pbg.lpg_delivery.model.entity;


import com.pbg.lpg_delivery.model.enums.CylinderType;
import com.pbg.lpg_delivery.model.request.LpgCylinderRequest;
import com.pbg.lpg_delivery.model.responses.Cylinder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "lpg_cylinders")
public class LpgCylinderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cylinderId;

    @Enumerated(EnumType.STRING)
    private CylinderType type;

    private Double weight;

    private Double price;

    private Integer stockQuantity;


    public static LpgCylinderEntity createNewCylinder(LpgCylinderRequest request) {
        return LpgCylinderEntity.builder()
                .type(request.type())
                .weight(request.weight())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();
    }


    public void updateStockAndPrice(LpgCylinderRequest request) {
        this.stockQuantity += request.stockQuantity();
        this.price = request.price();
    }

    public Cylinder mapToDto(){
        return Cylinder.builder()
                .cylinderType(type)
               .weight(weight)
               .price(price)
               .stockQuantity(stockQuantity)
               .build();
    }

}


