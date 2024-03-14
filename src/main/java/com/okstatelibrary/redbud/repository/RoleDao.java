package com.okstatelibrary.redbud.repository;

import org.springframework.data.repository.CrudRepository;

import com.okstatelibrary.redbud.security.Role;

public interface RoleDao extends CrudRepository<Role, Integer> {

    Role findByName(String name);
}