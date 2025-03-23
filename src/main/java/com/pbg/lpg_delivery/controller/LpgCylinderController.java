package com.pbg.lpg_delivery.controller;

import com.pbg.lpg_delivery.common.ResponseWrapper;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import com.pbg.lpg_delivery.model.request.LpgCylinderRequest;
import com.pbg.lpg_delivery.model.responses.Cylinder;
import com.pbg.lpg_delivery.service.CylinderService;
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

import java.util.List;

@RestController
@RequestMapping("/stocks")
@Tag(name = "LPG Cylinder Stock Management", description = "APIs for managing LPG cylinder stocks")
public class LpgCylinderController {

    private static final Logger logger = LoggerFactory.getLogger(LpgCylinderController.class);
    private final CylinderService cylinderService;

    public LpgCylinderController(CylinderService cylinderService) {
        this.cylinderService = cylinderService;
    }

    @Operation(
            summary = "Add or update LPG cylinder stock",
            description = "Allows admin to add new LPG cylinders or update existing stock levels."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> addOrUpdateStocks(@RequestBody LpgCylinderRequest request) {
        try {
            logger.info("Processing LPG cylinder stock update: Type={}, Weight={}", request.type(), request.weight());

            String responseMessage = cylinderService.createOrUpdateCylinder(request);

            logger.info("Stock update successful: {}", responseMessage);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseWrapper.Builder<String>().data(responseMessage).build()
            );

        } catch (LpgException ex) {
            logger.error("Business exception occurred: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while updating LPG Cylinder stock", ex);
            throw ex;
        }
    }

    @Operation(
            summary = "Retrieve LPG cylinders by type",
            description = "Fetches available LPG cylinders based on the provided cylinder type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cylinders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No cylinders found for the given type"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<Cylinder>>> getCylindersByType(
            @Parameter(description = "Type of the LPG cylinder (e.g., DOMESTIC or COMMERCIAL)", example = "DOMESTIC")
            @RequestParam CylinderType type) {
        try {
            logger.info("Fetching cylinders of type: {}", type);

            var cylinders = cylinderService.getCylindersByType(type);
            if (cylinders.isEmpty()) {
                logger.warn("No cylinders found for type: {}", type);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper.Builder<List<Cylinder>>().data(List.of()).build());
            }

            logger.info("Successfully retrieved {} cylinders of type: {}", cylinders.size(), type);
            return ResponseEntity.ok(new ResponseWrapper.Builder<List<Cylinder>>().data(cylinders).build());

        } catch (LpgException ex) {
            logger.error("Error fetching cylinders of type: {}", ex.getErrorMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while fetching cylinders", ex);
            throw ex;
        }
    }
}
