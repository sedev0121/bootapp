package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.StatementDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Long> {
	
	DeliveryDetail findOneById(Long id);

	@Query(value = "SELECT * FROM delivery_detail WHERE code=:code", nativeQuery = true)
	List<DeliveryDetail> findDetailsByCode(String code);
	
	@Modifying
	@Query(value = "delete FROM delivery_detail WHERE code= :code", nativeQuery = true)
	void DeleteByCode(String code);
}
