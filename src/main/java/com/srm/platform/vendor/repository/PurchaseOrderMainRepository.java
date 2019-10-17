package com.srm.platform.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.srm.platform.vendor.model.PurchaseOrderMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseOrderMainRepository extends JpaRepository<PurchaseOrderMain, Long> {

	PurchaseOrderMain findOneById(String id);
	
	PurchaseOrderMain findOneByCode(String code);

}
