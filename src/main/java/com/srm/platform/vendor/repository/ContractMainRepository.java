package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.ContractMain;
import com.srm.platform.vendor.searchitem.SearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ContractMainRepository extends JpaRepository<ContractMain, Long> {

	ContractMain findOneByCode(String code);

	@Query(value = "select a.code, a.name from contract_main a left join purchase_order_main b on a.vendor_code=b.vencode where b.code=?1 and (b.company_id=a.company_id or a.company_id is null) and a.state=3 and b.orderdate>=a.start_date and b.orderdate<a.end_date", nativeQuery = true)
	Page<SearchItem> findAllOfPurchaseOrder(String orderCode, String search, Pageable pageable);
}
