package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.srm.platform.vendor.model.Unit;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UnitRepository extends JpaRepository<Unit, Long> {

	Unit findOneById(Long id);

	Unit findOneByName(String name);

	List<Unit> findByParentId(Long parentId);
}
