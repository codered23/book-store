package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "Role")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    public enum RoleName {
        USER, ADMIN
    }

    public String getName() {
        return name.name();
    }
}
