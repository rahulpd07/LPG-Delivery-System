package com.pbg.lpg_delivery.model.entity;



import com.pbg.lpg_delivery.model.request.FeedbackRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "feedbacks")
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Max(5)
    @Min(1)
    private Integer rating; // 1-5
    private String comments;
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    public static FeedbackEntity createFeedback(UserEntity user, OrderEntity order, FeedbackRequest request) {
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.user = user;
        feedback.order = order;
        feedback.rating = request.rating();
        feedback.comments = request.comments();
        return feedback;
    }
}


