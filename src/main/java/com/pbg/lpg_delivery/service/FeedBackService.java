package com.pbg.lpg_delivery.service;

import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.model.entity.FeedbackEntity;
import com.pbg.lpg_delivery.model.entity.OrderEntity;
import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import com.pbg.lpg_delivery.model.request.FeedbackRequest;
import com.pbg.lpg_delivery.repository.FeedbackRepository;
import com.pbg.lpg_delivery.repository.OrderRepository;
import com.pbg.lpg_delivery.repository.UserRepository;
import com.pbg.lpg_delivery.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedBackService.class);

    private final OrderRepository orderRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;


    public FeedBackService(OrderRepository orderRepository, FeedbackRepository feedbackRepository,UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void submitFeedback(Long orderId, FeedbackRequest feedbackRequest) {
        UserEntity user = AuthUtils.getCurrentUser(userRepository);

        OrderEntity order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new LpgException("FB-404", "Order not found or unauthorized"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new LpgException("FB-405", "Feedback can only be given after the order is delivered.");
        }

        FeedbackEntity feedback = FeedbackEntity.createFeedback(user, order, feedbackRequest);
        feedbackRepository.save(feedback);

        logger.info("Feedback submitted for order: {} by user: {}", orderId, user.getUsername());
    }

}
