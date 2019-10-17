package com.srm.platform.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.srm.platform.vendor.model.PurchaseInMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseInMainRepository extends JpaRepository<PurchaseInMain, Long> {

	PurchaseInMain findOneByCode(String code);
	
	PurchaseInMain findOneById(Long id);
}
