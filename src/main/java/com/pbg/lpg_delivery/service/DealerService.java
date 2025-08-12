package com.pbg.lpg_delivery.service;

import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.model.entity.DealerEntity;
import com.pbg.lpg_delivery.model.responses.Dealer;
import com.pbg.lpg_delivery.repository.DealerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealerService {

    private final DealerRepository dealerRepository;
    private static final Logger logger = LoggerFactory.getLogger(DealerService.class);


    public DealerService(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
    }

    public List<Dealer> searchAuthorizedDealers(String pincode, String companyName) {
        try {
            String trimmedPincode = (pincode != null) ? pincode.trim() : null;
            String trimmedCompanyName = (companyName != null) ? companyName.trim() : null;

            if (trimmedPincode != null && !trimmedPincode.isEmpty() && !trimmedPincode.matches("\\d{6}")) {
                    logger.warn("Invalid pincode format provided: {}", trimmedPincode);
                    throw new LpgException("LP-0001", "Invalid pincode format. Must be 6 digits.");
                }


            if ((trimmedPincode == null || trimmedPincode.isEmpty()) &&
                    (trimmedCompanyName == null || trimmedCompanyName.isEmpty())) {

                logger.debug("No filters provided. Fetching all authorized dealers.");
                List<DealerEntity> dealerEntities = dealerRepository.findAll()
                        .stream()
                        .filter(DealerEntity::isAuthorized)
                        .toList();

                logger.info("Found {} authorized dealers without filters.", dealerEntities.size());
                return dealerEntities.stream().map(DealerEntity::mapToDto).toList();
            }

            logger.debug("Searching dealers in repository with provided filters.");
            List<DealerEntity> dealerEntities = dealerRepository.searchDealersByPincodeOrName(
                    (trimmedPincode != null && !trimmedPincode.isEmpty()) ? trimmedPincode : null,
                    (trimmedCompanyName != null && !trimmedCompanyName.isEmpty()) ? trimmedCompanyName : null
            );

            if (dealerEntities.isEmpty()) {
                logger.warn("No authorized dealers found for filters - pincode: {}, companyName: {}", trimmedPincode, trimmedCompanyName);
            } else {
                logger.info("Found {} authorized dealers for filters - pincode: {}, companyName: {}", dealerEntities.size(), trimmedPincode, trimmedCompanyName);
            }

            return dealerEntities.stream().map(DealerEntity::mapToDto).toList();

        } catch (LpgException e) {
            throw e;
        } catch (Exception ex) {
            logger.error("Unexpected error while searching dealers - pincode: {}, companyName: {}", pincode, companyName, ex);
            throw new LpgException("LP-0010", "Unexpected error occurred while searching dealers.");
        }
    }

}

