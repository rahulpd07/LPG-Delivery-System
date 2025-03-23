package com.pbg.lpg_delivery.repository;

import com.pbg.lpg_delivery.model.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity,Long> {
}
