package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.VenPriceAdjustDetail;
import com.srm.platform.vendor.searchitem.VenPriceDetailItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VenPriceAdjustDetailRepository extends JpaRepository<VenPriceAdjustDetail, Long> {
	@Query(value = "SELECT * FROM venpriceadjust_detail WHERE mainid= :mainId", nativeQuery = true)
	List<VenPriceAdjustDetail> findByMainId(String mainId);

	@Query(value = "select b.name, b.specs, c.name puunit_name, a.* from venpriceadjust_detail a left join inventory b on a.cinvcode=b.code left join measurement_unit c on b.main_measure=c.code where  a.mainid= :mainId order by a.rowno", nativeQuery = true)
	List<VenPriceDetailItem> findDetailsByMainId(String mainId);

	@Modifying
	@Query(value = "delete FROM venpriceadjust_detail WHERE mainid= :mainId", nativeQuery = true)
	void DeleteByMainId(String mainId);

}
