package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.utility.SearchItem;
import com.srm.platform.vendor.utility.VendorSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VendorRepository extends JpaRepository<Vendor, Long> {
	@Query(value = "SELECT a.*, b.name unitname FROM vendor a left join unit b on a.unit_id=b.id WHERE a.code LIKE %?1% or a.name LIKE %?1% ", countQuery = "SELECT count(*) FROM vendor a left join unit b on a.unit_id=b.id WHERE a.code LIKE %?1% or a.name LIKE %?1% ", nativeQuery = true)
	Page<VendorSearchItem> findBySearchTerm(String search, Pageable pageable);

	@Query(value = "SELECT code, name FROM vendor WHERE "
			+ "abbrname LIKE %?1% or name LIKE %?1%", countQuery = "SELECT count(*) FROM vendor WHERE abbrname LIKE %?1% or name LIKE %?1%", nativeQuery = true)
	Page<SearchItem> findForSelect(String search, Pageable pageable);

	@Query(value = "SELECT code, name FROM vendor WHERE unit_id in ?1 and (abbrname LIKE %?2% or name LIKE %?2%)", countQuery = "SELECT count(*) FROM vendor WHERE unit_id in ?1 and (abbrname LIKE %?1% or name LIKE %?1%)", nativeQuery = true)
	Page<SearchItem> findForSelect(List<String> unitList, String search, Pageable pageable);

	Vendor findOneByCode(String code);

	@Query(value = "SELECT a.*, b.name unitname FROM vendor a left join unit b on a.unit_id=b.id WHERE b.id in ?2 and (a.code LIKE %?1% or a.name LIKE %?1%) ", countQuery = "SELECT count(*) FROM vendor a left join unit b on a.unit_id=b.id WHERE b.id in ?2 and (a.code LIKE %?1% or a.name LIKE %?1%) ", nativeQuery = true)
	Page<VendorSearchItem> findBySearchTerm(String search, List<String> unitList, Pageable pageable);

	@Query(value = "SELECT * FROM vendor where unit_id in ?1", nativeQuery = true)
	List<Vendor> findVendorsByUnitIdList(List<String> unitIdList);
}
