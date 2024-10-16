package com.school.school.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "DEPARTMENTS")
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled;

    @ManyToMany
    @JoinTable(
        name = "DEPARTMENTS_USERS",
        joinColumns = @JoinColumn(name = "DEPARTMENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    private List<User> users;

}
