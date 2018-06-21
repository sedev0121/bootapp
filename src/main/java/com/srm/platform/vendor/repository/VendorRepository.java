package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Vendor;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VendorRepository extends JpaRepository<Vendor, Long> {
	@Query(value = "SELECT * FROM vendor WHERE code LIKE %?1% or name LIKE %?1% ", nativeQuery = true)
	Page<Vendor> findBySearchTerm(String search, Pageable pageable);
}
