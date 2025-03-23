package com.pbg.lpg_delivery.model.entity;

import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import com.pbg.lpg_delivery.model.request.OrderRequest;
import com.pbg.lpg_delivery.model.responses.Order;
import com.pbg.lpg_delivery.model.responses.OrderDetails;
import com.pbg.lpg_delivery.model.responses.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private CylinderType cylinderType;

    private Double capacity;

    private Integer quantity;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // Pending, Delivered, Cancelled

    private LocalDateTime orderDate;

    private LocalDateTime deliveryDate;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private DeliveryEntity delivery;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentEntity payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private FeedbackEntity feedback;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreated(){
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    public OrderEntity mapToEntity(){
        return OrderEntity.builder()
                .orderId(orderId)
                .user(user)
                .cylinderType(cylinderType)
                .capacity(capacity)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .status(status)
                .orderDate(orderDate)
                .deliveryDate(deliveryDate)
                .delivery(delivery)
                .payment(payment)
                .feedback(feedback)
                .build();
    }

    public static OrderDetails toOrderDetails(List<OrderEntity> orders, boolean includeUserInfo) {
        if (orders.isEmpty()) {
            throw new LpgException("LP-404", "No orders found");
        }

        // Extract user info only once (from the first order)
        UserInfo userInfo = includeUserInfo ? new UserInfo(
                orders.get(0).getUser().getUsername(),
                orders.get(0).getUser().getEmail(),
                orders.get(0).getUser().getPhoneNumber(),
                orders.get(0).getUser().getAddress()
        ) : null;

        // Convert all OrderEntity objects into Order DTOs
        List<Order> orderList = orders.stream()
                .map(order -> new Order(
                        order.getCylinderType(),
                        order.getCapacity(),
                        order.getQuantity(),
                        order.getTotalPrice(),
                        order.getStatus(),
                        order.getOrderDate(),
                        order.getDeliveryDate()
                ))
                .toList();

        return new OrderDetails(userInfo, orderList);
    }

    public Order mapToOrder() {
        return Order.builder()
                .cylinderType(this.cylinderType)
                .capacity(this.capacity)
                .quantity(this.quantity)
                .totalPrice(this.totalPrice)
                .status(this.status)
                .orderDate(this.orderDate)
                .deliveryDate(this.deliveryDate)
                .build();
    }


    public static OrderEntity createOrder(OrderRequest orderRequest, UserEntity user, Double totalPrice) {
        return OrderEntity.builder()
                .cylinderType(orderRequest.cylinderType())
                .capacity(orderRequest.capacity())
                .quantity(orderRequest.quantity())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .user(user)
                .orderDate(LocalDateTime.now())
                .build();
    }

//    public void mapModifyOrderRequestToEntity(Double price){
//                 OrderEntity.builder()
//                .cylinderType(this.cylinderType)
//                .capacity(this.capacity)
//                .quantity(this.quantity)
//                .totalPrice(this.quantity * price)
//                .build();
//    }

    public static OrderDetails mapToOrderDetails(UserEntity user, List<OrderEntity> orders, boolean includeUserInfo) {
        UserInfo userInfo = null;

        if (includeUserInfo) {
            userInfo = new UserInfo(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress()
            );
        }

        List<Order> orderList = orders.stream()
                .map(order -> new Order(
                        order.getCylinderType(),
                        order.getCapacity(),
                        order.getQuantity(),
                        order.getTotalPrice(),
                        order.getStatus(),
                        order.getOrderDate(),
                        order.getDeliveryDate()
                )).toList();

        return new OrderDetails(userInfo, orderList);
    }



}




