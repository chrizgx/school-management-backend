package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.entity.Role;
import com.school.school.entity.Department;
import com.school.school.repository.DepartmentRepository;
import com.school.school.service.DepartmentService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@Slf4j
@RestController
@RequestMapping("/api/departments")
public class DepartmentsController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<Iterable<Department>> getDepartments() {
        try {
            Iterable<Department> departments = departmentRepository.findAll();

            return new ResponseEntity<>(departments, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable("id") Integer id, @RequestAttribute("role") Role requestorRole) {
        try {
            Department department = departmentService.findById(id, requestorRole);
            if (department == null) return ResponseEntity.notFound().build();

            return new ResponseEntity<>(department, HttpStatus.OK);
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> createDepartment(@RequestBody Department newDepartment, @RequestAttribute("role") Role requestorRole) {
        try {
            Department department = departmentService.create(newDepartment);

            if (department == null) return ResponseEntity.badRequest().build();
            return new ResponseEntity<>(department, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> updateDepartment(@PathVariable Integer id, @RequestBody Department updatedDepartment) {
        try {
            Department department = departmentService.update(id, updatedDepartment);
            
            if (department == null) return ResponseEntity.notFound().build();
            return new ResponseEntity<>(department, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> enable(@PathVariable("id") Integer id, @RequestAttribute("role") Role requestorRole) {
        return updateDepartmentStatus(id, true, requestorRole);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> disable(@PathVariable("id") Integer id, @RequestAttribute("role") Role requestorRole) {
        return updateDepartmentStatus(id, false, requestorRole);
    }


    private ResponseEntity<Department> updateDepartmentStatus(Integer id, boolean enable, Role requestorRole) {
        try {
            Department department;
            Boolean updated;
            
            updated = departmentService.setEnabled(id, enable);
            if (updated == null) return ResponseEntity.notFound().build();
            if (updated == false) return ResponseEntity.badRequest().build();
            
            department = departmentService.findById(id, requestorRole);
            return new ResponseEntity<>(department, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    

}
