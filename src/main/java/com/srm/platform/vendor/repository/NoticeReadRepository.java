package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.NoticeRead;

@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, Long> {

	@Query(value = "SELECT * FROM notice_read where notice_id=?1", nativeQuery = true)
	List<NoticeRead> findListByNoticeId(Long noticeid);

	@Query(value = "SELECT * FROM notice_read where notice_id=?1 and to_account_id=?2 limit 1", nativeQuery = true)
	NoticeRead findOneByNoticeAndAccount(Long noticeId, Long accountId);
}