package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.VenPriceAdjustDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VenPriceAdjustDetailRepository extends JpaRepository<VenPriceAdjustDetail, Long> {
	@Query(value = "SELECT * FROM venpriceadjust_detail WHERE mainid= :mainId", nativeQuery = true)
	List<VenPriceAdjustDetail> findByMainId(String mainId);

}
