package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Box;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface BoxRepository extends JpaRepository<Box, Long> {
	
	Box findOneById(Long id);
	Box findOneByCode(String code);
	
	List<Box> findByBoxClassId(Long id);
	
	@Query(value = "SELECT * FROM box WHERE box_class_id=?1 and code like %?2%", nativeQuery = true)
	Page<Box> findBySearchTerm2(Long classId, String search, Pageable pageable);

	@Query(value = "SELECT * FROM box where code LIKE %?1% and box_class_id=?2", nativeQuery = true)
	Page<Box> findBySearchTerm(String search, Long classId, Pageable pageable);
	
	@Query(value = "SELECT * FROM box where code LIKE %?1% and used=?2 and box_class_id=?3", nativeQuery = true)
	Page<Box> findBySearchAndUsed(String search, Integer used, Long classId, Pageable pageable);

	@Query(value = "SELECT * FROM box where code LIKE %?1% and state=?2 and box_class_id=?3", nativeQuery = true)
	Page<Box> findBySearchAndState(String search, Integer state, Long classId, Pageable pageable);
	
	@Query(value = "SELECT * FROM box where code LIKE %?1% and used=?2 and state=?3 and box_class_id=?4", nativeQuery = true)
	Page<Box> findBySearchUsedAndState(String search, Integer used, Integer state, Long classId, Pageable pageable);
	
}
