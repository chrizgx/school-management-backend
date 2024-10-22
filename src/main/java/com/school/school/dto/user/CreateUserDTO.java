package com.school.school.dto.user;

import com.school.school.entity.Role;
import com.school.school.security.annotations.Sanitize;

public class CreateUserDTO {

    @Sanitize
    private String firstName;

    @Sanitize
    private String lastName;

    @Sanitize
    private String email;

    @Sanitize
    private String password;

    private Role role;

    private boolean enabled;
}
