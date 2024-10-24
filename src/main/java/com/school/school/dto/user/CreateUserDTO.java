package com.school.school.dto.user;

import com.school.school.entity.Role;
import com.school.school.security.annotations.Sanitize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateUserDTO {

    @Sanitize
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 20, message = "First name must be between 1 and 20 characters")
    private String firstName;

    @Sanitize
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 20, message = "Last name must be between 1 and 20 characters")
    private String lastName;

    @Sanitize
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Sanitize
    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 20, message = "Password must be between 1 and 20 characters")
    private String password;

    @Sanitize
    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Enabled is required")
    private boolean enabled;
}
