package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.ContractMain;
import com.srm.platform.vendor.searchitem.ContractSearchItem;
import com.srm.platform.vendor.searchitem.SearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ContractMainRepository extends JpaRepository<ContractMain, Long> {

	ContractMain findOneByCode(String code);

	@Query(value = "select a.code, a.price_type, a.base_price, a.floating_price, concat('[', a.code, '][', DATE_FORMAT(a.start_date, '%Y-%m-%d'), '~', DATE_FORMAT(a.end_date, '%Y-%m-%d'), ']', a.name) name from contract_main a left join purchase_order_main b on a.vendor_code=b.vencode where b.code=?1 and (b.company_id=a.company_id or a.company_id is null) and a.state=2 and now()>=a.start_date and now()<a.end_date "
			+ "and a.code in (select code from contract_detail where inventory_code in (select inventory_code from purchase_order_detail where main_id=?1))", nativeQuery = true)
	Page<ContractSearchItem> findAllOfPurchaseOrder(String orderId, String search, Pageable pageable);
}
