package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.NoticeRead;

@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, Long> {

	List<NoticeRead> findAllByNoticeId(Long noticeid);
}