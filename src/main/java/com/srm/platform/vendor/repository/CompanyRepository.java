package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Company;
import com.srm.platform.vendor.searchitem.SearchItem;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	@Query(value = "SELECT id code, name FROM company", nativeQuery = true)
	Page<SearchItem> findForSelect(Pageable pageable);
	
	Company findOneById(Long id);
	
}