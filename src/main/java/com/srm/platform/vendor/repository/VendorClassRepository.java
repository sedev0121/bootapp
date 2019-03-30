package com.srm.platform.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.model.VendorClass;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface VendorClassRepository extends JpaRepository<VendorClass, Long> {

	VendorClass findOneByCode(String code);
}
