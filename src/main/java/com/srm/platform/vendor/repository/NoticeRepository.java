package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Box;
import com.srm.platform.vendor.model.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
	Notice findOneById(Long id);

	@Query(value = "SELECT * FROM notice where class_id=?1", nativeQuery = true)
	List<Notice> findAllByClassId(Long classId);	

}