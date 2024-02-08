package com.example.bookstore.service.impl;

import com.example.bookstore.model.Role;
import com.example.bookstore.repository.role.RoleRepository;
import com.example.bookstore.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;

    @Override
    public Role save(Role role) {
        return repository.save(role);
    }

    @Override
    public Role getByName(Role.RoleName roleName) {
        return repository.getByRoleName(roleName);
    }
}
