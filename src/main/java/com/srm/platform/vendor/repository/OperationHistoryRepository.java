package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.OperationHistory;

@Repository
public interface OperationHistoryRepository extends JpaRepository<OperationHistory, Long> {
	
	OperationHistory findOneById(Long id);
	
	@Query(value = "SELECT * FROM operation_history where target_type=?1 and target_id=?2 order by creat_date desc", nativeQuery = true)
	List<OperationHistory> findByTarget(String targetType, String targetId);

}