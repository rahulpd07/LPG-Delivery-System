package com.pbg.lpg_delivery.service;

import com.pbg.lpg_delivery.common.Role;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import com.pbg.lpg_delivery.model.request.ModifyOrderRequest;
import com.pbg.lpg_delivery.model.request.OrderRequest;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import com.pbg.lpg_delivery.model.entity.LpgCylinderEntity;
import com.pbg.lpg_delivery.model.entity.OrderEntity;
import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.model.responses.Cylinder;
import com.pbg.lpg_delivery.model.responses.Order;
import com.pbg.lpg_delivery.model.responses.OrderDetails;
import com.pbg.lpg_delivery.model.responses.UserInfo;
import com.pbg.lpg_delivery.repository.LpgCylinderRepository;
import com.pbg.lpg_delivery.repository.OrderRepository;
import com.pbg.lpg_delivery.repository.UserRepository;
import com.pbg.lpg_delivery.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {


    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final LpgCylinderRepository lpgCylinderRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, LpgCylinderRepository lpgCylinderRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.lpgCylinderRepository = lpgCylinderRepository;
    }

    private static final EnumSet<CylinderType> VALID_CYLINDER_TYPES = EnumSet.of(CylinderType.COMMERCIAL, CylinderType.DOMESTIC);

    public static void validateCylinderTypeAndCapacity(CylinderType cylinderType, Double capacity) {
        if (cylinderType == null) {
            throw new LpgException("LP-404", "Cylinder type cannot be null.");
        }

        if (!VALID_CYLINDER_TYPES.contains(cylinderType)) {
            throw new LpgException("LP-404", "Cylinder type must be either 'DOMESTIC' or 'COMMERCIAL'.");
        }

        if (capacity <= 0) {
            throw new LpgException("LP-404", "Capacity must be greater than zero.");
        }
        if (!isValidCapacity(cylinderType, capacity)) {
            throw new LpgException("LP-404", "The capacity does not match the cylinder type.");
        }
    }

    private static boolean isValidCapacity(CylinderType type, Double capacity) {
        return type.getCapacity() == capacity;
    }

    public static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new LpgException("LH-404", "Quantity must be greater than zero.");
        }
    }


    @Transactional
    public void createOrder(OrderRequest orderRequest) {
        try {
            UserEntity user = AuthUtils.getCurrentUser(userRepository);


            validateCylinderTypeAndCapacity(orderRequest.cylinderType(), orderRequest.capacity());
            validateQuantity(orderRequest.quantity());
            if (!isValidCapacity(orderRequest.cylinderType(), orderRequest.capacity())) {
                throw new LpgException("LP-406", "Invalid capacity for " + orderRequest.cylinderType() +
                        " cylinder. Expected: " + orderRequest.cylinderType().getCapacity() + " kg.");
            }

            LpgCylinderEntity availableCylinder = lpgCylinderRepository
                    .findAvailableCylinderByTypeAndWeight(orderRequest.cylinderType(), orderRequest.capacity())
                    .orElseThrow(() -> new LpgException("LP-102", "No available stock for " + orderRequest.cylinderType() + " cylinder."));

            if (availableCylinder.getStockQuantity() < orderRequest.quantity()) {
                throw new LpgException("LP-103", "Insufficient stock available.");
            }

            availableCylinder.setStockQuantity(availableCylinder.getStockQuantity() - orderRequest.quantity());
            lpgCylinderRepository.save(availableCylinder);

            double totalPrice = availableCylinder.getPrice() * orderRequest.quantity();

            OrderEntity order = OrderEntity.createOrder(orderRequest, user, totalPrice);
            orderRepository.save(order);

            logger.info("Order placed successfully for user {}", user.getUsername());

        } catch (LpgException e) {
            logger.error("Business error occurred while placing order: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while placing order", e);
            throw new ParentException("LP-999", "An unexpected error occurred. Please contact support.");
        }
    }


    public OrderDetails getOrderDetails(String username, String phoneNumber) {
        try {
            UserEntity user = AuthUtils.getCurrentUser(userRepository);

            return switch (user.getRole()) {
                case CUSTOMER -> {
                    if (username != null && !user.getUsername().equals(username)) {
                        throw new LpgException("LP-404", "Unauthorized: Customers can only view their own orders.");
                    }
                    if (phoneNumber != null && !user.getPhoneNumber().equals(phoneNumber)) {
                        throw new LpgException("LP-404", "Unauthorized: Customers can only view their own orders.");
                    }

                    List<OrderEntity> orders = orderRepository.findByUsername(user.getUsername());
                    yield OrderEntity.toOrderDetails(orders, false);
                }
                case ADMIN -> {
                    if (username == null || phoneNumber == null) {
                        throw new LpgException("LP-402", "Admin must provide username and phone number.");
                    }
                    List<OrderEntity> orders = orderRepository.findByUsernameAndPhoneNumber(username, phoneNumber);
                    yield OrderEntity.toOrderDetails(orders, true);
                }
                default -> throw new LpgException("LP-403", "Unauthorized access.");
            };
        } catch (LpgException ex) {
            logger.error("Business error occurred while getting orders: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while getting orders", ex);
            throw new ParentException("LP-999", "An unexpected error occurred. Please contact support.");
        }
    }



    public List<OrderDetails> getAllOrdersByDateRange(LocalDate startDate, LocalDate endDate) {

        try {
            UserEntity user = AuthUtils.getCurrentUser(userRepository);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

            if (user.getRole() != Role.ADMIN) {
                throw new LpgException("LP-403", "Unauthorized access");
            }

            List<OrderEntity> orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);

            // Group orders by user and map to OrderDetails
            return orders.stream()
                    .collect(Collectors.groupingBy(OrderEntity::getUser))
                    .entrySet()
                    .stream()
                    .map(entry -> new OrderDetails(
                            new UserInfo(entry.getKey().getUsername(), entry.getKey().getEmail(), entry.getKey().getPhoneNumber(), entry.getKey().getAddress()),
                            entry.getValue().stream().map(OrderEntity::mapToOrder).toList()
                    ))
                    .toList();
        } catch (LpgException ex) {
            logger.error("Business error occurred while getting orders by date range: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while getting orders by date range", ex);
            throw new ParentException("LP-999", "An unexpected error occurred. Please contact support.");
        }

    }

    @Transactional
    public void modifyOrder(Long orderId, ModifyOrderRequest request) {

        try {
            UserEntity user = AuthUtils.getCurrentUser(userRepository);

            validateCylinderTypeAndCapacity(request.cylinderType(), request.capacity());
            validateQuantity(request.quantity());
            OrderEntity order = orderRepository.findByIdAndUser(orderId, user)
                    .orElseThrow(() -> new LpgException("LP-404", "Order not found or unauthorized"));

            if(order.getStatus() != OrderStatus.PENDING) {
                throw new LpgException("LP-0010","order cannot be modified now");
            }

            LpgCylinderEntity newCylinder = lpgCylinderRepository.findAvailableCylinderByTypeAndWeight(
                            request.cylinderType(), request.capacity())
                    .orElseThrow(() -> new LpgException("LP-406", "No available stock for the selected cylinder type and capacity."));

            if (newCylinder.getStockQuantity() < request.quantity()) {
                throw new LpgException("LP-407", "Not enough stock available for the selected cylinder.");
            }

            if (ChronoUnit.HOURS.between(order.getOrderDate(), LocalDateTime.now()) > 24) {
                throw new LpgException("LP-405", "Modification period expired. Orders can only be modified within 24 hours.");
            }

            LpgCylinderEntity oldCylinder = lpgCylinderRepository.findByTypeAndWeight(order.getCylinderType(), order.getCapacity())
                    .orElseThrow(() -> new LpgException("LP-408", "Original cylinder record not found"));

            oldCylinder.setStockQuantity(oldCylinder.getStockQuantity() + order.getQuantity());
            newCylinder.setStockQuantity(newCylinder.getStockQuantity() - request.quantity());
            lpgCylinderRepository.save(oldCylinder);
            lpgCylinderRepository.save(newCylinder);


            order.setCylinderType(request.cylinderType());
            order.setCapacity(request.capacity());
            order.setQuantity(request.quantity());
            order.setTotalPrice(newCylinder.getPrice() * request.quantity());


            orderRepository.save(order);
            logger.info("Order modified successfully for user: {}", user.getUsername());
        }catch (LpgException ex){
            logger.error("Business error occurred while modifying order: {}", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            logger.error("");
            throw new ParentException("MORE-101","some internal error occurred while modifying order");
        }

    }


    @Transactional
    public void cancelOrder(final Long orderId) {

        try {
            OrderEntity orderEntity = orderRepository.findById(orderId)
                    .orElseThrow(() -> new LpgException("LP-404", "Order not found."));

            if (orderEntity.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
                throw new LpgException("LP-405", "Orders can only be cancelled within 24 hours of placing.");
            }

            UserEntity loggedInUser = AuthUtils.getCurrentUser(userRepository);

            if (!loggedInUser.getRole().name().equals("ADMIN") && !orderEntity.getUser().equals(loggedInUser)) {
                throw new LpgException("LP-407", "You do not have permission to cancel this order.");
            }

            orderRepository.deleteById(orderId);

        }catch (LpgException ex){
            logger.error("Business error occurred while cancelling order: {}", ex.getMessage());
            throw ex;
        }catch (Exception ex){
            logger.error("some internal server error occurred while canceling the orderId {}",orderId);
            throw new ParentException("CO-102","internal Error occurred while canceling the order");
        }
        }

    }


