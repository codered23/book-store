package com.example.bookstore.service;

import com.example.bookstore.model.Role;

public interface RoleService {
    Role save(Role role);

    Role getByName(Role.RoleName roleName);
}
