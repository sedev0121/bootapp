package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.DeliveryMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DeliveryMainRepository extends JpaRepository<DeliveryMain, Long> {
	
	DeliveryMain findOneById(Long id);
	
	DeliveryMain findOneByCode(String code);
	
}
