package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Store;
import com.srm.platform.vendor.searchitem.SearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StoreRepository extends JpaRepository<Store, Long> {

	Store findOneById(Long id);
	
	@Query(value = "SELECT * FROM store a left join company b on a.company_id=b.id where (a.name LIKE %?1% or b.name LIKE %?1%)", nativeQuery = true)
	Page<Store> findBySearchTerm(String search, Pageable pageable);
	
	@Query(value = "SELECT * FROM store a left join company b on a.company_id=b.id where (a.name LIKE %?1% or b.name LIKE %?1%) and is_use_in_srm=?2", nativeQuery = true)
	Page<Store> findBySearchTerm(String search, Integer usedState, Pageable pageable);
	
	@Query(value = "SELECT id code, name FROM store where name like %?1%", nativeQuery = true)
	Page<SearchItem> findForSelect(String search, Pageable pageable);
	
	@Query(value = "SELECT id code, name FROM store where company_id=?1 and name like %?2%", nativeQuery = true)
	Page<SearchItem> findForSelectOfCompany(Long companyId, String search, Pageable pageable);
}
