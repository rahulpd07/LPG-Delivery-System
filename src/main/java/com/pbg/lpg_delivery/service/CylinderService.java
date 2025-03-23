package com.pbg.lpg_delivery.service;

import com.pbg.lpg_delivery.common.Role;
import com.pbg.lpg_delivery.controller.OrderController;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import com.pbg.lpg_delivery.model.request.LpgCylinderRequest;
import com.pbg.lpg_delivery.model.entity.LpgCylinderEntity;
import com.pbg.lpg_delivery.model.responses.Cylinder;
import com.pbg.lpg_delivery.repository.LpgCylinderRepository;
import com.pbg.lpg_delivery.repository.UserRepository;
import com.pbg.lpg_delivery.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CylinderService {

    private static final Logger logger = LoggerFactory.getLogger(CylinderService.class);

    private final LpgCylinderRepository lpgCylinderRepository;
    private final UserRepository userRepository;
    public CylinderService(LpgCylinderRepository lpgCylinderRepository, UserRepository userRepository) {
        this.lpgCylinderRepository = lpgCylinderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createOrUpdateCylinder(LpgCylinderRequest request) {
        try {
            UserEntity user = AuthUtils.getCurrentUser(userRepository);
            if (user.getRole() != Role.ADMIN) {
                throw new LpgException("LP-403", "Unauthorized access. Only ADMIN can modify the stock.");
            }


            if (!isValidCylinder(request.type(), request.weight())) {
                throw new LpgException("LP-4001", "Invalid cylinder type or weight. Only COMMERCIAL (18.5 kg) or DOMESTIC (14.5 kg) are allowed.");
            }

            lpgCylinderRepository.findByTypeAndWeight(request.type(), request.weight())
                    .ifPresentOrElse(cylinder -> {
                        cylinder.updateStockAndPrice(request);
                        lpgCylinderRepository.save(cylinder);
                    }, () -> {
                        LpgCylinderEntity newCylinder = LpgCylinderEntity.createNewCylinder(request);
                        lpgCylinderRepository.save(newCylinder);
                    });

            return "Cylinder stock successfully updated.";

        } catch (LpgException ex) {
            logger.error("Business exception: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error updating cylinder stock for type: {}, weight: {}. Error: {}",
                    request.type(), request.weight(), ex.getMessage(), ex);
            throw new LpgException("LP-5000", "Internal Server Error. Please try again later.");
        }
    }

    private boolean isValidCylinder(CylinderType type, double weight) {
        return (type == CylinderType.COMMERCIAL && weight == 18.5) || (type == CylinderType.DOMESTIC && weight == 14.5);
    }


    public List<Cylinder> getCylindersByType(CylinderType type) {
        try {
            UserEntity user = AuthUtils.getCurrentUser(userRepository);
            if (user.getRole() != Role.ADMIN) {
                throw new LpgException("LP-403", "Unauthorized access. Only ADMIN can view the stock.");
            }
            List<LpgCylinderEntity> cylinders = lpgCylinderRepository.findByType(type);

            if (cylinders.isEmpty()) {
                throw new LpgException("LP-404", "No cylinders found for type: " + type);
            }

            return cylinders.stream()
                    .map(LpgCylinderEntity::mapToDto)
                    .toList();

        } catch (LpgException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ParentException("LP-5000", "Unexpected error occurred while fetching cylinders.");
        }
    }


}