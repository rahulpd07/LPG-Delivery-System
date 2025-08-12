package com.pbg.lpg_delivery.repository;

import com.pbg.lpg_delivery.model.entity.DealerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerRepository extends JpaRepository<DealerEntity, Long> {

    @Query("""
   SELECT d FROM DealerEntity d
   LEFT JOIN d.dealerAddress dd
   WHERE d.authorized = true
   AND (:pincode IS NULL OR dd.pincode = :pincode)
   AND (:companyName IS NULL OR LOWER(d.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))
""")
    List<DealerEntity> searchDealersByPincodeOrName(@Param("pincode") String pincode,
                                                    @Param("companyName") String companyName);


}

