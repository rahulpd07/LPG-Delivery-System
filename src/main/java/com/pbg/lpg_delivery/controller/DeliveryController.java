package com.pbg.lpg_delivery.controller;

import com.pbg.lpg_delivery.common.ResponseWrapper;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.model.responses.OrderDetails;
import com.pbg.lpg_delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/delivery")
@Tag(name = "Delivery Management", description = "APIs for managing LPG cylinder deliveries")
public class DeliveryController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryController.class);
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(
            summary = "Assign an order to a delivery person",
            description = "Allows an admin to assign a specific order to a delivery person."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID or username"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to perform this action"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}/assign/{userName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> assignOrderToDeliveryPerson(
            @Parameter(description = "ID of the order to be assigned", example = "101")
            @PathVariable Long orderId,
            @Parameter(description = "Username of the delivery person", example = "delivery_user123")
            @PathVariable String userName) {
        try {
            deliveryService.assignOrderToDelivery(orderId, userName);
            return ResponseEntity.ok(
                    new ResponseWrapper.Builder<String>().data("Order assigned successfully.").build());
        } catch (LpgException ex) {
            logger.error("Error assigning order: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while assigning order: {}", ex.getMessage(), ex);
            throw new ParentException("An unexpected error occurred while assigning order", "ST-5002");
        }
    }

    @Operation(
            summary = "Mark an order as delivered",
            description = "Allows an admin or a delivery person to mark an order as delivered."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as delivered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to perform this action"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}/mark-delivered")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_PERSON')")
    public ResponseEntity<ResponseWrapper<String>> markOrderAsDelivered(
            @Parameter(description = "ID of the order to be marked as delivered", example = "101")
            @PathVariable Long orderId) {
        try {
            deliveryService.markAsDelivered(orderId);
            return ResponseEntity.ok(
                    new ResponseWrapper.Builder<String>().data("The Order has been delivered Successfully.").build());
        } catch (LpgException ex) {
            logger.error("Error marking order as delivered: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while marking order as delivered: {}", ex.getMessage(), ex);
            throw new ParentException("An unexpected error occurred", "ST-5003");
        }
    }

    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<OrderDetails>>> getAllAssignedOrder(){
        try {
            List<OrderDetails> orderDetailsList = deliveryService.getAllAssignedOrders();
            return ResponseEntity.ok(
                    new ResponseWrapper.Builder<List<OrderDetails>>()
                           .data(orderDetailsList)
                           .build());
        } catch (LpgException ex) {
            logger.error("Error retrieving all assigned orders: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while retrieving all assigned orders: {}", ex.getMessage(), ex);
            throw new ParentException("An unexpected error occurred while retrieving assigned orders", "ST-5004");
        }
    }
}
