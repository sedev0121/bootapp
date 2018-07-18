package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.utility.PurchaseOrderDetailSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {

	PurchaseOrderDetail findOneById(Long id);

	@Query(value = "select * from purchase_order_detail where code= :code order by rowno", nativeQuery = true)
	List<PurchaseOrderDetail> findDetailsByCode(String code);

	@Query(value = "select a.*, d.code vendorcode, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname from purchase_order_detail a left join purchase_order_main b on a.code = b.code left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code left join measurement_unit e on c.main_measure=e.code where b.vencode=?1 and b.srmstate=2 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", countQuery = "select count(*) from purchase_order_detail a left join purchase_order_main b on a.code = b.code left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code where b.vencode=?1 and b.srmstate=2 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", nativeQuery = true)
	Page<PurchaseOrderDetailSearchItem> findDetailsForShip(String vendor, String code, String inventory,
			Pageable pageable);

	@Query(value = "select a.*, d.code vendorcode, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname from purchase_order_detail a left join purchase_order_main b on a.code = b.code left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code left join measurement_unit e on c.main_measure=e.code where b.vencode=?1 and b.srmstate=2 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", nativeQuery = true)
	Page<PurchaseOrderDetailSearchItem> findDetailsForExport(String vendor, String code, String inventory,
			Pageable pageable);

	@Query(value = "select d.code vendorcode, a.*, (a.quantity-ifnull(a.shipped_quantity,0)) remain_quantity, d.name vendorname, c.name inventoryname, c.specs, e.name unitname from purchase_order_detail a left join purchase_order_main b on a.code = b.code left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code left join measurement_unit e on c.main_measure=e.code where d.unit_id in ?1 and (d.code like %?2% or d.name like %?2%) and  b.srmstate=2 and a.code like %?3% and (c.name like %?4% or c.code like %?4%)", countQuery = "select count(*) from purchase_order_detail a left join purchase_order_main b on a.code = b.code left join inventory c on a.inventorycode=c.code left join vendor d on b.vencode=d.code where d.unit_id in ?1 and (d.code like %?2% or d.name like %?2%) and  b.srmstate=2 and a.code like %?3% and (c.name like %?4% or c.code like %?4%)", nativeQuery = true)
	Page<PurchaseOrderDetailSearchItem> findDetailsForBuyerShip(List<String> unitList, String vendor, String code,
			String inventory, Pageable pageable);
}
