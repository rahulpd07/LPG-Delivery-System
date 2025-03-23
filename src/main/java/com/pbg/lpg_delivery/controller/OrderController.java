package com.pbg.lpg_delivery.controller;

import com.pbg.lpg_delivery.common.ResponseWrapper;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.model.request.ModifyOrderRequest;
import com.pbg.lpg_delivery.model.request.OrderRequest;
import com.pbg.lpg_delivery.model.responses.OrderDetails;
import com.pbg.lpg_delivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for managing customer orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Create a new order",
            description = "Allows customers to place an LPG cylinder order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.createOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseWrapper.Builder<String>().data("Order placed successfully, sit back and relax!").build());
        } catch (LpgException ex) {
            logger.error("Order placement failed: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during order placement: {}", ex.getMessage());
            throw ex;
        }
    }

    @Operation(
            summary = "Get order details",
            description = "Retrieves order details based on username or phone number."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<OrderDetails>> getOrderDetails(
            @Parameter(description = "Username of the customer") @RequestParam(required = false) String userName,
            @Parameter(description = "Phone number of the customer") @RequestParam(required = false) String phoneNumber) {
        try {
            var orders = orderService.getOrderDetails(userName, phoneNumber);
            return ResponseEntity.ok(new ResponseWrapper.Builder<OrderDetails>().data(orders).build());
        } catch (LpgException ex) {
            logger.error("Order retrieval failed: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred: {}", ex.getMessage());
            throw ex;
        }
    }

    @Operation(
            summary = "Get all orders within a date range",
            description = "Fetches all orders placed within a specified start and end date."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper<List<OrderDetails>>> getAllOrdersByDateRange(
            @Parameter(description = "Start date for filtering orders", example = "2024-02-01")
            @RequestParam LocalDate startDate,
            @Parameter(description = "End date for filtering orders", example = "2024-02-15")
            @RequestParam LocalDate endDate) {
        try {
            var orders = orderService.getAllOrdersByDateRange(startDate, endDate);
            return ResponseEntity.ok(new ResponseWrapper.Builder<List<OrderDetails>>().data(orders).build());
        } catch (LpgException ex) {
            logger.error("Order retrieval failed!: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred!: {}", ex.getMessage());
            throw ex;
        }
    }

    @Operation(
            summary = "Modify an existing order",
            description = "Allows customers to update order details before it is processed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order modified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid modification request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseWrapper<String>> modifyOrder(
            @Parameter(description = "Order ID to be modified") @PathVariable Long orderId,
            @RequestBody ModifyOrderRequest request) {
        try {
            orderService.modifyOrder(orderId, request);
            return ResponseEntity.ok(new ResponseWrapper.Builder<String>().data("Order modified successfully!").build());
        } catch (LpgException ex) {
            logger.error("Order modification failed!: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Order modification failed due to some internal error: {}", ex.getMessage());
            throw ex;
        }
    }

    @Operation(
            summary = "Cancel an order",
            description = "Allows customers to cancel an order before it is dispatched."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order canceled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseWrapper<String>> cancelOrder(
            @Parameter(description = "Order ID to be canceled") @PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            String successMessage = String.format("Order deleted successfully for orderId %d", orderId);
            return ResponseEntity.ok(new ResponseWrapper.Builder<String>().data(successMessage).build());
        } catch (LpgException ex) {
            logger.error("Error occurred while deleting the order: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Order was not deleted due to an internal error: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
