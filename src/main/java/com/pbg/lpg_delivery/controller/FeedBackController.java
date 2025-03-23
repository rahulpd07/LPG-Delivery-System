package com.pbg.lpg_delivery.controller;

import com.pbg.lpg_delivery.common.ResponseWrapper;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.model.request.FeedbackRequest;
import com.pbg.lpg_delivery.service.FeedBackService;
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

@RestController
@RequestMapping("v1/feedback")
@Tag(name = "Feedback Management", description = "APIs for submitting customer feedback on orders")
public class FeedBackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedBackController.class);

    private final FeedBackService feedBackService;

    public FeedBackController(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    @Operation(
            summary = "Submit feedback for an order",
            description = "Allows a customer to submit feedback for a delivered order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID or feedback content"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to perform this action"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{orderId}")
    public ResponseEntity<ResponseWrapper<String>> submitFeedback(
            @Parameter(description = "ID of the order for which feedback is being submitted", example = "101")
            @PathVariable Long orderId,
            @RequestBody FeedbackRequest feedbackRequest) {
        try {
            feedBackService.submitFeedback(orderId, feedbackRequest);
            return ResponseEntity.ok(
                    new ResponseWrapper.Builder<String>().data("Feedback submitted successfully.").build());
        } catch (LpgException ex) {
            logger.error("Error submitting feedback: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while submitting feedback", ex);
            throw new ParentException("An unexpected error occurred while submitting feedback", "FB-5001");
        }
    }

}
