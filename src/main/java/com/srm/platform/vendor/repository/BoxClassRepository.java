package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.BoxClass;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface BoxClassRepository extends JpaRepository<BoxClass, Long> {

	BoxClass findOneById(Long id);
	
	@Query(value = "SELECT * FROM box_class WHERE name like %?1%", nativeQuery = true)
	List<BoxClass> findBySearchTerm(String search);
	
}
