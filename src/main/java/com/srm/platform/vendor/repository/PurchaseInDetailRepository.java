package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.searchitem.PurchaseInDetailItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseInDetailRepository extends JpaRepository<PurchaseInDetail, Long> {

	PurchaseInDetail findOneById(Long id);

	@Query(value = "select b.name inventoryname, b.specs, b.main_measure unitname, c.closed_quantity, a.* from purchase_in_detail a left join inventory b on a.inventory_code=b.code left join (select b.id, sum(a.closed_quantity) closed_quantity from statement_detail a left join purchase_in_detail b on a.purchase_in_detail_id=b.id left join statement_main c on a.code=c.code where c.state=3 group by b.id) c on a.id=c.id where a.code= :code order by a.rowno", nativeQuery = true)
	List<PurchaseInDetailItem> findDetailsByCode(String code);

	@Query(value = "select * from purchase_in_detail where code=?1 and rowno=?2", nativeQuery = true)
	PurchaseInDetail findOneByCodeAndRowno(String code, Integer rowno);

}
