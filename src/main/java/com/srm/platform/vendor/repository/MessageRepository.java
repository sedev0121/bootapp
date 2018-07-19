package com.srm.platform.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.srm.platform.vendor.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	Message findOneById(Long id);

}