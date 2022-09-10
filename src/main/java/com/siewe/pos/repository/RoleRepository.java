package com.siewe.pos.repository;

import com.siewe.pos.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    default Role findOne(Long id) {
        return (Role) findById(id).orElse(null);
    }
    Role findByName(String role);
}
