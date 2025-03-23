package com.pbg.lpg_delivery.repository;

import com.pbg.lpg_delivery.model.entity.DeliveryEntity;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryEntity,Long> {
    @Query("SELECT d.order.id FROM DeliveryEntity d WHERE d.deliveryPerson.id = :deliveryPersonId AND d.order.status = :status")
    List<Long> findOrderIdsByDeliveryPersonAndStatus(@Param("deliveryPersonId") Long deliveryPersonId,
                                                     @Param("status") OrderStatus status);

}
