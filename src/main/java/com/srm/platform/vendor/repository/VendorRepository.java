package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Vendor;
import com.srm.platform.vendor.searchitem.SearchItem;
import com.srm.platform.vendor.searchitem.VendorSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VendorRepository extends JpaRepository<Vendor, Long> {
	@Query(value = "SELECT a.*, c.state FROM vendor a left join account c on a.code=c.username WHERE (a.code LIKE %?1% or a.name LIKE %?1%) and c.username is null", countQuery = "SELECT count(*) FROM vendor a WHERE a.code LIKE %?1% or a.name LIKE %?1% ", nativeQuery = true)
	Page<VendorSearchItem> findBySearchTerm(String search, Pageable pageable);

	@Query(value = "SELECT code, name FROM vendor WHERE (abbrname LIKE %?1% or code LIKE %?1% or name LIKE %?1%) and code in (select vendor_code from account where vendor_code is not null)", countQuery = "SELECT count(*) FROM vendor WHERE (abbrname LIKE %?1% or code LIKE %?1% or name LIKE %?1%) and code in (select vendor_code from account where vendor_code is not null)", nativeQuery = true)
	Page<SearchItem> findCreatedVendors(String search, Pageable pageable);
	
	Vendor findOneByCode(String code);

	@Query(value = "SELECT * FROM vendor a left join vendor_class b on a.sort_code=b.code where (a.name LIKE %?1% or a.code LIKE %?1% or a.abbrname LIKE %?1%) and (b.code LIKE %?2% or b.name LIKE %?2%)", nativeQuery = true)
	Page<Vendor> findVendorsBySearchTerm(String vendor, String vendorClass, Pageable pageable);

	@Query(value = "SELECT a.*, '' unitname, c.stop_date FROM vendor a left join account c on a.code=c.vendor_code where a.code in ?1", nativeQuery = true)
	List<VendorSearchItem> findVendorsByCodeList(String[] codeList);

	@Query(value = "SELECT code, name FROM vendor WHERE (abbrname LIKE %?1% or code LIKE %?1% or name LIKE %?1%) and code in ?2", countQuery = "SELECT count(*) FROM vendor WHERE (abbrname LIKE %?1% or code LIKE %?1% or name LIKE %?1%) and code in ?2", nativeQuery = true)
	Page<SearchItem> findCreatedVendorsByCodeList(String search, List<String> codeList, Pageable pageable);
}
