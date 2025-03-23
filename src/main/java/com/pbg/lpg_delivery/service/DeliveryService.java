package com.pbg.lpg_delivery.service;

import com.pbg.lpg_delivery.common.Role;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.model.entity.DeliveryEntity;
import com.pbg.lpg_delivery.model.entity.OrderEntity;
import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.model.enums.DeliveryStatus;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import com.pbg.lpg_delivery.model.responses.OrderDetails;
import com.pbg.lpg_delivery.repository.DeliveryRepository;
import com.pbg.lpg_delivery.repository.OrderRepository;
import com.pbg.lpg_delivery.repository.UserRepository;
import com.pbg.lpg_delivery.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);
    private  final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    public DeliveryService(UserRepository userRepository,OrderRepository orderRepository,
                           DeliveryRepository deliveryRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public void assignOrderToDelivery(Long orderId, String userName) {

        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new LpgException("Order not found", "ST-1003"));

            UserEntity deliveryPerson = userRepository.findByUsername(userName)
                    .orElseThrow(() -> new LpgException("Delivery person not found", "ST-1004"));

            if (deliveryPerson.getRole() != Role.DELIVERY_PERSON) {
                logger.warn("User {} is not a valid delivery person", userName);
                throw new LpgException("User is not a delivery person", "ST-1005");
            }

            if (order.getDelivery() != null) {
                logger.warn("Order {} is already assigned to a delivery person", orderId);
                throw new LpgException("Order is already assigned to a delivery person", "ST-1006");
            }


            DeliveryEntity delivery = DeliveryEntity.createDelivery(order, deliveryPerson);

            order.setDelivery(delivery);
            order.setStatus(OrderStatus.IN_TRANSIT);
            orderRepository.save(order);

            logger.info("Order {} assigned to delivery person {}. Expected delivery: {}", orderId, userName, delivery.getExpectedDeliveryDate());

        }catch (LpgException ex){
            logger.error("Error occured  while assigning the orderId {} to delivery person {}",orderId,userName);
            throw ex;
        }catch (Exception ex){
            logger.error("Error occured while assigning the orderId {} to delivery person {}",orderId,userName);
            throw new ParentException("DLBE-101","some internal error occurred while assigning the orderId to delivery person");
        }

    }

    @Transactional
    public void markAsDelivered(Long orderId) {

        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new LpgException("Order not found", "ST-1003"));

            if (order.getDelivery() == null) {
                throw new LpgException("No delivery assigned to this order", "ST-1007");
            }

            UserEntity currentUser = AuthUtils.getCurrentUser(userRepository);


            if (currentUser.getRole() == Role.DELIVERY_PERSON &&
                    !order.getDelivery().getDeliveryPerson().equals(currentUser)) {
                throw new LpgException("Unauthorized: Only the assigned delivery person or admin can update the delivery status", "ST-1008");
            }


            DeliveryEntity delivery = order.getDelivery();
            delivery.setStatus(DeliveryStatus.DELIVERED);
            delivery.setDeliveryDate(LocalDateTime.now().withNano(0));

            order.setDeliveryDate(LocalDateTime.now().withNano(0));
            order.setStatus(OrderStatus.DELIVERED);

            deliveryRepository.save(delivery);
            orderRepository.save(order);
            logger.info("Order {} marked as delivered by {}", orderId, currentUser.getUsername());
        }catch (LpgException ex){
            logger.error("Error occurred while marking for delivery for the orderId {}",orderId);
            throw ex;
        }catch (Exception ex){
            logger.error("Some internal Error occurred while marking for delivery for the orderId {}",orderId);
            throw new ParentException("DLBE-102","some internal error occurred while marking for delivery");
        }

    }

    public List<OrderDetails> getAllAssignedOrders() {
        UserEntity currentUser = AuthUtils.getCurrentUser(userRepository);

        if (currentUser.getRole() != Role.DELIVERY_PERSON) {
            throw new LpgException("Unauthorized: Only Delivery Person can access this functionality", "ST-1009");
        }

        List<Long> assignedOrderIds = deliveryRepository.findOrderIdsByDeliveryPersonAndStatus(
                currentUser.getUserId(), OrderStatus.IN_TRANSIT
        );


        List<OrderEntity> assignedOrders = orderRepository.findByOrderIdInAndStatus(assignedOrderIds, OrderStatus.IN_TRANSIT);

        Map<UserEntity, List<OrderEntity>> groupedOrders = assignedOrders.stream()
                .collect(Collectors.groupingBy(OrderEntity::getUser));

        return groupedOrders.entrySet().stream()
                .map(entry -> OrderEntity.mapToOrderDetails(entry.getKey(), entry.getValue(), true))
                .toList();
    }


}
