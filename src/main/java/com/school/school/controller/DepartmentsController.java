package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.entity.Role;
import com.school.school.dto.department.DepartmentDTO;
import com.school.school.entity.Department;
import com.school.school.repository.DepartmentRepository;
import com.school.school.response.NotFoundWrapper;
import com.school.school.response.ResponseWrapper;
import com.school.school.service.DepartmentService;
import com.school.school.exception.UserAlreadyEnrolledException;

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
    public ResponseEntity<ResponseWrapper<Iterable<DepartmentDTO>>> getDepartments() {
        Iterable<DepartmentDTO> departments = departmentService.findAll();
        return ResponseEntity.ok(new ResponseWrapper<>(departments, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<DepartmentDTO>> getDepartmentById(@PathVariable("id") Integer id, @RequestAttribute("role") Role requestorRole) {
        DepartmentDTO department = departmentService.findById(id, requestorRole);
        if (department == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ResponseWrapper<>(department, true));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<DepartmentDTO>> createDepartment(@RequestBody Department newDepartment, @RequestAttribute("role") Role requestorRole) {
        DepartmentDTO department = departmentService.create(newDepartment);

        if (department == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(department, true));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<DepartmentDTO>> updateDepartment(@PathVariable Integer id, @RequestBody Department updatedDepartment) {
        DepartmentDTO department = departmentService.update(id, updatedDepartment);
        
        if (department == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundWrapper<>("department", id));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseWrapper<>(department, true));
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<DepartmentDTO>> enable(@PathVariable("id") Integer id, @RequestAttribute("role") Role requestorRole) {
        return updateDepartmentStatus(id, true, requestorRole);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<DepartmentDTO>> disable(@PathVariable("id") Integer id, @RequestAttribute("role") Role requestorRole) {
        return updateDepartmentStatus(id, false, requestorRole);
    }


    private ResponseEntity<ResponseWrapper<DepartmentDTO>> updateDepartmentStatus(Integer id, boolean enable, Role requestorRole) {
        DepartmentDTO department;
        Boolean updated;
        
        updated = departmentService.setEnabled(id, enable);
        if (updated == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundWrapper<>("department", id));
        if (updated == false) return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>("Department ID#" + id + " is already " + (enable ? "enabled" : "disabled"), false));
        
        department = departmentService.findById(id, requestorRole);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseWrapper<>(department, true));
    }

    @PostMapping("/{id}/student/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<ResponseWrapper<Void>> enrollStudent(@PathVariable("id") Integer departmentId, @PathVariable("userId") Integer userId) {
        try {
            Boolean enroll = departmentService.enrollStudent(departmentId, userId);
            if (enroll) return ResponseEntity.ok(new ResponseWrapper<>("User ID#" + userId + " successfully enrolled to Department ID#" + departmentId, true));
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>("User is either not a student or does not exist.", false));
        } catch (UserAlreadyEnrolledException e) {
            log.info("Error: " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>("User ID#" + userId + " is already enrolled to Department ID#" + departmentId, false));
        }
    }

}
