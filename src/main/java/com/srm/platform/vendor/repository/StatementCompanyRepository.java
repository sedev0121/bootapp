package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.StatementCompany;
import com.srm.platform.vendor.searchitem.SearchItem;

@Repository
public interface StatementCompanyRepository extends JpaRepository<StatementCompany, Long> {
	@Query(value = "SELECT id code, name FROM statement_company", nativeQuery = true)
	Page<SearchItem> findForSelect(Pageable pageable);	
	
	StatementCompany findOneById(Long id);
	
	
	@Query(value = "SELECT * FROM statement_company where id in (select statement_company_id from company where id in ?1)", nativeQuery = true)
	List<StatementCompany> findStatementCompanys(List<Long> companyIds);
}