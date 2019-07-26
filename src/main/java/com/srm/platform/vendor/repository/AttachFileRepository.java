package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.AttachFile;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface AttachFileRepository extends JpaRepository<AttachFile, Long> {
	
	AttachFile findOneById(Long id);
	
	@Query(value = "SELECT * from attach_file where type=?1 and code=?2 and row_no=?3", nativeQuery = true)
	AttachFile findOneByTypeCodeAndRowNo(String type, String code, Integer rowNo);
	
	
	@Query(value = "SELECT * from attach_file where type=?1 and code=?2", nativeQuery = true)
	List<AttachFile> findAllByTypeCode(String type, String code);
}
