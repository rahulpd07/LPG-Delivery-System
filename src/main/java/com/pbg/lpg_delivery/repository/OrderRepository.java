package com.pbg.lpg_delivery.repository;

import com.pbg.lpg_delivery.model.entity.OrderEntity;
import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    @Query("SELECT o FROM OrderEntity o WHERE o.user.username = :username")
    List<OrderEntity> findByUsername(@Param("username") String username);

    @Query("SELECT o FROM OrderEntity o WHERE o.user.username = :username AND o.user.phoneNumber = :phoneNumber")
    List<OrderEntity> findByUsernameAndPhoneNumber(@Param("username") String username,
                                                   @Param("phoneNumber") String phoneNumber);

    @Query("SELECT o FROM OrderEntity o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<OrderEntity> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = :orderId AND o.user = :user")
    Optional<OrderEntity> findByIdAndUser(@Param("orderId") Long orderId, @Param("user") UserEntity user);

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.delivery WHERE o.id = :orderId")
    Optional<OrderEntity> findByOrderIdWithDelivery(@Param("orderId") Long orderId);

    @Query("SELECT o FROM OrderEntity o WHERE o.status = 'IN_TRANSIT'")
    List<OrderEntity> findAllAssignedOrder();

    List<OrderEntity> findByOrderIdInAndStatus(List<Long> orderIds, OrderStatus status);



}


