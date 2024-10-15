package com.school.school.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="USERS")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private Role role;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled;

    @ManyToMany
    @JoinTable(
        name = "USERS_DEPARTMENTS",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "DEPARTMENT_ID")
    )
    private List<Department> departments;

    // public boolean isEnabled() {
    //     return this.enabled;
    // }
}
