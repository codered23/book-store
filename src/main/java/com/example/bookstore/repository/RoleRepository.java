package com.example.bookstore.repository;

import com.example.bookstore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByRoleName(Role.RoleName roleName);
}
