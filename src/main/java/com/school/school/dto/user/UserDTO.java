package com.school.school.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import com.school.school.entity.Role;
import com.school.school.entity.Department;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Boolean enabled;
    // private List<DepartmentMiniDTO> departments;
}
