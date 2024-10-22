package com.school.school.mapper;

import com.school.school.entity.Department;

import org.springframework.stereotype.Component;

import com.school.school.dto.department.*;

@Component
public class DepartmentMapper {
    public DepartmentDTO toDepartmentDTO(Department department) {
        return new DepartmentDTO(department.getId(), department.getName(), department.getDescription(), department.isEnabled());
    }
}
