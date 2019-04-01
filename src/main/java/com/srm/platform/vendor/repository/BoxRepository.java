package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.Inventory;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface BoxRepository extends JpaRepository<Box, Long> {
	
	Box findOneById(Long id);
	
	@Query(value = "SELECT * FROM box WHERE box_class_id=?1 and code like %?2%", nativeQuery = true)
	Page<Box> findBySearchTerm(Long classId, String search, Pageable pageable);

}
