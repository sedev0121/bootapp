package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.NegotiationDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface NegotiationDetailRepository extends JpaRepository<NegotiationDetail, Long> {
	
	NegotiationDetail findOneById(Long id);
	
	@Query(value = "SELECT * FROM negotiation_detail WHERE code=:code", nativeQuery = true)
	List<NegotiationDetail> findDetailsByCode(String code);
}
