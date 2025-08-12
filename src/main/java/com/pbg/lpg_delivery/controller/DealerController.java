package com.pbg.lpg_delivery.controller;

import com.pbg.lpg_delivery.common.ResponseWrapper;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.model.responses.Dealer;
import com.pbg.lpg_delivery.service.DealerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/dealers")
public class DealerController {

    private final DealerService dealerService;

    private static final Logger logger = LoggerFactory.getLogger(DealerController.class);

    public DealerController(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<List<Dealer>>> searchDealers(
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String companyName) {

        logger.info("Searching dealers with filters - pincode: {}, companyName: {}", pincode, companyName);

        try {
            var dealers = dealerService.searchAuthorizedDealers(pincode, companyName);

            if (dealers.isEmpty()) {
                logger.warn("No authorized dealers found for filters - pincode: {}, companyName: {}", pincode, companyName);
                return ResponseEntity.noContent().build();
            }
            logger.info("Found {} authorized dealers for filters - pincode: {}, companyName: {}", dealers.size(), pincode, companyName);
            return ResponseEntity.ok(
                    new ResponseWrapper.Builder<List<Dealer>>().data(dealers).build()
            );
        } catch (LpgException ex) {
            logger.error("Business error while searching dealers - pincode: {}, companyName: {}. Error: {}",
                    pincode, companyName, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while searching dealers - pincode: {}, companyName: {}",
                    pincode, companyName, ex);
            throw ex;
        }
    }

}

