package com.srm.platform.vendor.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.model.StatementMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StatementMainRepository extends JpaRepository<StatementMain, Long> {

	StatementMain findOneByCode(String code);

	StatementMain findOneByInvoiceCode(String code);

	@Query(value = "SELECT * FROM statement_main where state=?1", nativeQuery = true)
	List<StatementMain> findAllPending(Integer state);
	
    
}
