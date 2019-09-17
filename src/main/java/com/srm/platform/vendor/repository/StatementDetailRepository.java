package com.srm.platform.vendor.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.searchitem.StatementDetailItem;
import com.srm.platform.vendor.searchitem.StatementPendingDetail;
import com.srm.platform.vendor.searchitem.StatementPendingItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StatementDetailRepository extends JpaRepository<StatementDetail, Long> {

	StatementDetail findOneById(Long id);

	@Query(value = "SELECT * FROM statement_detail WHERE code= :code order by row_no", nativeQuery = true)
	List<StatementDetail> findByCode(String code);

	@Query(value = "select a.*, d.code pi_code, c.auto_id pi_auto_id, c.state pi_state, c.tax_rate pi_tax_rate, c.tax_price pi_tax_price, d.vendor_code pi_vendor_code, d.store_code pi_store_code, d.date pi_date, e.code inventory_code, e.name inventory_name, e.specs, e.main_measure unitname, "
			+ "c.quantity pi_quantity, c.tax_price, c.tax_cost, pom.code po_code, po.row_no po_row_no, po.confirmed_memo confirmed_memo, "
			+ "delivery.code delivery_code, delivery.row_no delivery_row_no, delivery.delivered_quantity "
			+ "from statement_detail a left join statement_main b on a.code=b.code "
			+ "left join purchase_in_detail c on a.pi_detail_id=c.id left join purchase_in_main d on c.main_id=d.id "
			+ "left join purchase_order_detail po on c.po_id=po.main_id and c.po_row_no=po.row_no "
			+ "left join purchase_order_main pom on po.main_id=pom.id "
			+ "left join delivery_detail delivery on c.delivery_code=delivery.code and c.delivery_row_no=delivery.row_no "
			+ "left join inventory e on c.inventory_code=e.code "
			+ "where a.code=?1 order by a.row_no", nativeQuery = true)
	List<StatementDetailItem> findDetailsByCode(String code);
	
	
	@Query(value = "select b.vendor_code, b.type, c.statement_company_id from purchase_in_detail a "
			+ "left join purchase_in_main b on a.code=b.code left join company c on b.company_code=c.code "
			+ "where a.state=0 and b.company_code is not null and b.date < ?1 and b.vendor_code in (select vendor_code from account where role='ROLE_VENDOR' and vendor_code is not null) GROUP BY b.vendor_code, b.type, c.statement_company_id", nativeQuery = true)
	List<StatementPendingItem> findAllPendingData(Date filterDate);
	
	@Query(value = "select a.id, a.code from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join company c on b.company_code=c.code left join statement_company d on c.statement_company_id=d.id "
			+ "where a.state=0 and b.company_code is not null and b.vendor_code = ?1 and d.id = ?2 and b.type = ?3 and b.date < ?4 order by a.code, a.row_no", nativeQuery = true)
	List<StatementPendingDetail> findAllPendingDetail(String vendorCode, Long statementCompanyId, String type, Date filterDate);

}
