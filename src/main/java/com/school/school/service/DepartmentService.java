package com.school.school.service;

import com.school.school.repository.DepartmentRepository;
import com.school.school.repository.UserRepository;
import com.school.school.entity.Role;
import com.school.school.entity.User;
import com.school.school.entity.Department;
import com.school.school.exception.UserAlreadyEnrolledException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class DepartmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Iterable<Department> findAll() {
        Iterable<Department> departments = departmentRepository.findAll();
        return departments;
    }

    public Department findById(Integer id, Role requestorRole) {
        Optional<Department> department = departmentRepository.findById(id);

        if (!department.isPresent()) return null;

        // Privacy check
        if (requestorRole == Role.STUDENT) department.get().setUsers(null);

        return department.get();
    }

    public Department create(Department newDepartment) {
        return departmentRepository.save(newDepartment);
    }

    public Department update(Integer id, Department updatedDepartment) {
        Optional<Department> existingDepartment = departmentRepository.findById(id);

        if (existingDepartment.isPresent() == false) return null;
        Department department = existingDepartment.get();

        if (updatedDepartment.getName() != null) department.setName(updatedDepartment.getName());
        if (updatedDepartment.getDescription() != null) department.setDescription(updatedDepartment.getDescription());

        return departmentRepository.save(department);
    }

    // Expected Result
    // NULL: Not department found with id given
    // FALSE: Cannot enable/disable since dept. is already enabled/disabled
    // TRUE: Operation completed
    public Boolean setEnabled(Integer id, boolean value) {
        Optional<Department> existingDepartment = departmentRepository.findById(id);
        if (existingDepartment.isPresent() == false) return null;
        Department department = existingDepartment.get();

        if (department.isEnabled() == value) return false;

        department.setEnabled(value);
        departmentRepository.save(department);
        return true;
    }
    
    // Expected Results
    // NULL: No user or department found with the id given
    // TRUE: Action completed successfully
    // FALSE: Not allowed, usually when the userId given corresponds to a non-student profile role
    public Boolean enrollStudent(Integer departmentId, Integer userId) {
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
        Optional<User> userOptional = userRepository.findById((Long) userId.longValue());

        if (departmentOptional.isEmpty()) return null;
        if (userOptional.isEmpty()) return null;
        if (userOptional.get().getRole() != Role.STUDENT) return false;

        Department department = departmentOptional.get();
        User user = userOptional.get();

        if (department.getUsers().contains(user)) {
            throw new UserAlreadyEnrolledException("User with ID " + userId + " is already enrolled in Department with ID " + departmentId + " (\"" + department.getName() + "\")");
        }

        // Enroll user to department
        department.getUsers().add(user);
        departmentRepository.save(department);

        return true;
    }

}
