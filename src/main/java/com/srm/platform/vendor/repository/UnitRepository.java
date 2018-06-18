package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Unit;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UnitRepository extends JpaRepository<Unit, Long> {

	Unit findOneById(Long id);

	Unit findOneByName(String name);

	List<Unit> findByParentId(Long parentId);

	@Query(value = "SELECT GROUP_CONCAT(lv SEPARATOR ', ') id FROM (SELECT @pv\\:=(SELECT GROUP_CONCAT(id SEPARATOR ', ') FROM unit WHERE parent_id IN (@pv)) AS lv FROM unit JOIN (SELECT @pv\\:= ?1)tmp WHERE parent_id IN (@pv)) a", nativeQuery = true)
	String findChildrenByGroupId(Long groupId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from unit where id in ?1", nativeQuery = true)
	void deleteByChildIds(String[] childIds);
}
