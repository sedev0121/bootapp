package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.StatementDetail;
import com.srm.platform.vendor.utility.StatementDetailItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StatementDetailRepository extends JpaRepository<StatementDetail, Long> {

	StatementDetail findOneById(Long id);

	@Query(value = "SELECT * FROM statement_detail WHERE code= :code", nativeQuery = true)
	List<StatementDetail> findByCode(String code);

	@Query(value = "select a.*, c.quantity-ifnull(a.closed_quantity, 0) remain_quantity, d.type, c.code purchase_in_code, c.po_code, c.nat_price, c.nat_tax_rate, "
			+ "c.rowno, c.quantity, c.price, c.cost, c.tax_price, c.tax_cost, c.nat_tax_price, c.material_quantity, c.material_tax_price, "
			+ "f.name unitname, d.date purchase_in_date, e.code inventorycode, e.name inventoryname, e.specs "
			+ "from statement_detail a left join statement_main b on a.code=b.code left join purchase_in_detail c on a.purchase_in_detail_id=c.id "
			+ "left join purchase_in_main d on c.code=d.code left join inventory e on c.inventory_code=e.code "
			+ "left join measurement_unit f on e.main_measure=f.code where a.code=?1 order by c.code, c.rowno", nativeQuery = true)
	List<StatementDetailItem> findDetailsByCode(String code);

}
