package com.srm.platform.vendor.repository;

import org.springframework.data.repository.CrudRepository;

import com.srm.platform.vendor.model.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Long> {

}
