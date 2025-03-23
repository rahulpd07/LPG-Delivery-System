package com.pbg.lpg_delivery.repository;


import com.pbg.lpg_delivery.model.entity.LpgCylinderEntity;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LpgCylinderRepository extends JpaRepository<LpgCylinderEntity,Long> {

    Optional<LpgCylinderEntity> findByTypeAndWeight(CylinderType type, Double weight);
    List<LpgCylinderEntity> findByType(CylinderType type);

    @Query("SELECT c FROM LpgCylinderEntity c WHERE c.type = :type AND c.weight = :weight")
    Optional<LpgCylinderEntity> findAvailableCylinderByTypeAndWeight(
            @Param("type") CylinderType type,
            @Param("weight") Double weight
    );

}

