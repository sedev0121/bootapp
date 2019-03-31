package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Store;
import com.srm.platform.vendor.searchitem.StoreSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StoreRepository extends JpaRepository<Store, Long> {

	Store findOneById(Long id);
	
	@Query(value = "SELECT a.*, b.name company_name FROM store a left join company b on a.company_id=b.id where (a.name LIKE %?1% or b.name LIKE %?1%)", nativeQuery = true)
	Page<StoreSearchItem> findBySearchTerm(String search, Pageable pageable);
}
