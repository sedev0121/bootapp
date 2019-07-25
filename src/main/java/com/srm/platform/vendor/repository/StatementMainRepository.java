package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.StatementMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StatementMainRepository extends JpaRepository<StatementMain, Long> {

	StatementMain findOneByCode(String code);

	StatementMain findOneByInvoiceCode(String code);

    @Transactional
    @Modifying
	@Query(value = "update statement_main set state=3 where state=2 and company_id in ?1", nativeQuery = true)
	void bulkReviewByCompany(List<Long> companyList);
    
    @Transactional
    @Modifying
	@Query(value = "update statement_main set state=3 where state=2 and vendor_code in ?1", nativeQuery = true)
	void bulkReviewByVendor(List<String> vendorList);
    
    @Transactional
    @Modifying
	@Query(value = "update statement_main set state=4 where state=3 and company_id in ?1", nativeQuery = true)
	void bulkDeployByCompany(List<Long> companyList);
    
    @Transactional
    @Modifying
	@Query(value = "update statement_main set state=4 where state=3 and vendor_code in ?1", nativeQuery = true)
	void bulkDeployByVendor(List<String> vendorList);
}
