package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.BoxClass;
import com.srm.platform.vendor.searchitem.SearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface BoxClassRepository extends JpaRepository<BoxClass, Long> {

	BoxClass findOneById(Long id);
	
	BoxClass findOneByCode(String code);
	
	@Query(value = "SELECT * FROM box_class WHERE name like %?1% order by code asc", nativeQuery = true)
	List<BoxClass> findBySearchTerm(String search);
	
	@Query(value = "SELECT id code, name FROM box_class where code like %?1% or name like %?1% order by code asc", nativeQuery = true)
	Page<SearchItem> findForSelect(String search, Pageable pageable);
	
	@Query(value = "SELECT * FROM box_class order by code asc", nativeQuery = true)
	List<BoxClass> findAllNodes();
	
}
