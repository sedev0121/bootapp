package com.srm.platform.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
	Notice findOneById(Long id);

}