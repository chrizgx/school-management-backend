package com.school.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.school.entity.Department;

import java.util.Optional;
import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long>{
    Optional<Department> findById(Integer id);

}
