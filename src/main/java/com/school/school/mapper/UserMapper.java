package com.school.school.mapper;

import com.school.school.entity.User;

import org.springframework.stereotype.Component;

import com.school.school.dto.user.*;

@Component
public class UserMapper {
    public UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    public User toUser(CreateUserDTO user) {
        return new User(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getRole(), user.isEnabled());
    }
}
