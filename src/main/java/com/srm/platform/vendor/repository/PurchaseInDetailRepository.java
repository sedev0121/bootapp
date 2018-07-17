package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.utility.PurchaseInDetailItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseInDetailRepository extends JpaRepository<PurchaseInDetail, Long> {

	PurchaseInDetail findOneById(Long id);

	@Query(value = "select b.name inventoryname, b.specs, d.name unitname, c.closed_quantity, a.* from purchase_in_detail a left join inventory b on a.inventorycode=b.code left join (select b.id, sum(a.closed_quantity) closed_quantity from statement_detail a left join purchase_in_detail b on a.purchase_in_detail_id=b.id left join statement_main c on a.code=c.code where c.state=3 group by b.id) c on a.id=c.id left join measurement_unit d on b.main_measure=d.code where a.code= :code order by a.rowno", nativeQuery = true)
	List<PurchaseInDetailItem> findDetailsByCode(String code);

	@Query(value = "select a.*, m.name unitname, d.closed_quantity, b.date, c.name inventoryname,c.specs from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join inventory c on a.inventorycode=c.code left join measurement_unit m on c.main_measure=m.code left join statement_detail d on d.purchase_in_detail_id=a.id where d.closed_quantity is null and b.vendorcode=?1 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", countQuery = "select count(*) from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join inventory c on a.inventorycode=c.code left join statement_detail d on a.id=d.purchase_in_detail_id where d.closed_quantity is null and b.vendorcode=?1 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", nativeQuery = true)
	Page<PurchaseInDetailItem> findForSelect(String vendor, String code, String inventory, Pageable pageable);
}
