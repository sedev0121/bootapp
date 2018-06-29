package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.VenPriceAdjustMain;
import com.srm.platform.vendor.utility.VenPriceAdjustSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VenPriceAdjustMainRepository extends JpaRepository<VenPriceAdjustMain, Long> {

	VenPriceAdjustMain findOneByCcode(String ccode);

	@Query(value = "select distinct a.*, c.code vendorcode, c.name vendorname, e.realname makername, f.realname verifiername from venpriceadjust_main a left join venpriceadjust_detail b on a.ccode = b.mainid "
			+ "left join vendor c on a.cvencode=c.code left join inventory d on b.cinvcode=d.code "
			+ "left join account e on a.maker_id=e.id left join account f on a.cverifier_id=f.id "
			+ "where (c.name like CONCAT('%',:vendor, '%') or c.code like CONCAT('%',:vendor, '%')) "
			+ "and (ifnull(d.name,'') like CONCAT('%',:inventory, '%') or ifnull(d.code,'') like CONCAT('%',:inventory, '%'))", countQuery = "select count(distinct a.ccode) from venpriceadjust_main a left join venpriceadjust_detail b on a.ccode = b.mainid left join vendor c on a.cvencode=c.code left join inventory d on b.cinvcode=d.code left join account e on a.maker_id=e.id left join account f on a.cverifier_id=f.id where (c.name like CONCAT('%',:vendor, '%') or c.code like CONCAT('%',:vendor, '%')) and (ifnull(d.name,'') like CONCAT('%',:inventory, '%') or ifnull(d.code,'') like CONCAT('%',:inventory, '%'))", nativeQuery = true)
	Page<VenPriceAdjustSearchItem> findBySearchTerm(String vendor, String inventory, Pageable pageable);

	@Query(value = "select distinct a.*, c.code vendorcode, c.name vendorname, e.realname makername, f.realname verifiername from venpriceadjust_main a left join venpriceadjust_detail b on a.ccode = b.mainid "
			+ "left join vendor c on a.cvencode=c.code left join inventory d on b.cinvcode=d.code "
			+ "left join account e on a.maker_id=e.id left join account f on a.cverifier_id=f.id "
			+ "where c.code= :vendorCode and a.iverifystate>1 "
			+ "and (ifnull(d.name,'') like CONCAT('%',:inventory, '%') or ifnull(d.code,'') like CONCAT('%',:inventory, '%'))", nativeQuery = true)
	Page<VenPriceAdjustSearchItem> findBySearchTermForVendor(String vendorCode, String inventory, Pageable pageable);

}
