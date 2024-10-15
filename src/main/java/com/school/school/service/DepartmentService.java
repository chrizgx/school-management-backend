package com.school.school.service;

import com.school.school.repository.DepartmentRepository;
import com.school.school.repository.UserRepository;
import com.school.school.entity.Role;
import com.school.school.entity.User;
import com.school.school.entity.Department;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DepartmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
    
}
