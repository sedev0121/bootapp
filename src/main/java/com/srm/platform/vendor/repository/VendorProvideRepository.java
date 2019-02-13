package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.model.UnitProvide;
import com.srm.platform.vendor.model.VendorProvide;
import com.srm.platform.vendor.utility.PermissionItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VendorProvideRepository extends JpaRepository<VendorProvide, Long> {


	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from vendor_provide where vendor_code = ?1", nativeQuery = true)
	void deleteByVendorCodeAndUnitId(String vendorCode);
	

}
