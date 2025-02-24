package com.cardone.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardone.dscatalog.entities.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    
}
