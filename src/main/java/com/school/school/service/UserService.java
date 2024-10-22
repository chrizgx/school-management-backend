package com.school.school.service;

import com.school.school.entity.Role;
import com.school.school.entity.User;
import com.school.school.dto.user.*;
import com.school.school.repository.UserRepository;
import com.school.school.security.JwtUtil;
import com.school.school.service.CustomUserDetailsService;
import com.school.school.exception.*;
import com.school.school.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CustomUserDetailsService userDetailsService; // Inject the UserDetailsService

    @Autowired
    private JwtUtil jwtUtil;

    public Boolean authenticate(String email, String password) {
        log.info("authenticate(-)");
        UserDetails user = userDetailsService.loadUserByUsername(email);

        if (user != null && user.getPassword().equals(password)) {
            log.info("authenticate(- completed -)");
            return true;
        }
        log.info("authenticate(- wrong mail or password -)");
        return false;
    }

    public String generateToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            Long id = user.get().getId();
            Role role = user.get().getRole();
            return jwtUtil.generateToken(email, id, role);
        }

        return null;
    }

    public UserDTO getUser(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) return null;

        User user = optionalUser.get();

        return userMapper.toUserDTO(user);
    }

    public UserDTO createUserGuard(User user, Integer requestorId, Role requestorRole) {
        if (requestorRole == Role.HELP_DESK && ( user.getRole() == Role.ADMIN || user.getRole() == Role.HELP_DESK ) ) {
            log.info("createUserGuard(- ID# " + requestorId + " illegally tried to create user with " + user.getRole() + " permissions.");
            throw new UnauthorizedRoleActionException("Help Desk users can only create teacher and student accounts.");
        }

        User newUser = userDetailsService.createUser(user);
        return userMapper.toUserDTO(newUser);
    }

    // 
    public UserDTO updateUserGuard(Integer id, User user, Integer requestorId, Role requestorRole) {
        Optional<User> existingUser = userRepository.findById(id);

        if (!existingUser.isPresent()) return null;
        User userToUpdate = existingUser.get(); 

        // Authentication Logic starts here
        // Admin can edit everyone's profile and ALL attributes
        if (requestorRole == Role.ADMIN) return updateUser(userToUpdate.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getRole(), user.isEnabled());

        // Help desk can edit all role's profile except Admin's and Help Desks (including themselves)
        // Help desk CANNOT edit user role.
        if (requestorRole == Role.HELP_DESK && (userToUpdate.getRole() == Role.ADMIN || userToUpdate.getRole() == Role.HELP_DESK) ) {
            log.info("updateUserGuard(- ID# " + requestorId + " illegally tried to update profile ID#" + user.getId());
            throw new UnauthorizedRoleActionException("You have no permission to update ID#" + id);
        }
        
        return updateUser(userToUpdate.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), null, user.isEnabled());
    }

    public UserDTO updateUser(Long id, String firstName, String lastName, String email, String password, Role role, Boolean enabled) {
        Optional<User> existingUser = userRepository.findById(id);

        if (!existingUser.isPresent()) return null;

        User user = existingUser.get();

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (password != null) user.setPassword(password);
        if (role != null) user.setRole(role);
        if (enabled != null) user.setEnabled(enabled);

        User updatedUser = userRepository.save(user);
        return userMapper.toUserDTO(updatedUser);
    }

    // Method restricted for password recovery by ADMIN and HELP_DESK users
    // Expected Results
    // NULL: There is no user with this id
    // TRUE: Updated
    // FALSE: ACTION NOT ALLOWED
    public Boolean updatePasswordGuard(Integer id, String newPassword, Integer requestorId, Role requestorRole) {
        // Block null results
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) return null;

        User user = existingUser.get();

        // Authenticate
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.HELP_DESK) return false; // Double check
        if (requestorRole == Role.HELP_DESK && user.getRole() == Role.ADMIN) return false; // Block HELP DESK editing ADMIN
        if (requestorRole == Role.HELP_DESK && user.getRole() == Role.HELP_DESK) return false; // Block HELP DESK editing other HELP DESK

        // Perform Action
        return updatePassword(id, newPassword);
    }

    // Expected Results
    // NULL: There is no user with this id
    // TRUE: Updated
    // FALSE: Password Authentication Failed
    public Boolean updatePasswordGuard(Integer id, String password, String newPassword) {
        // Block null results
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) return null;

        User user = existingUser.get();

        // Authenticate
        if (user.getPassword().equals(password) == false) return false;

        // Perform action
        return updatePassword(id, newPassword);
    }

    // Expected Results
    // NULL: There is no user with this id
    // TRUE: Updated
    public Boolean updatePassword(Integer id, String password) {
        Optional<User> existingUser = userRepository.findById(id);
        if (!existingUser.isPresent()) return null;
        
        User user = existingUser.get();

        user.setPassword(password);
        userRepository.save(user);

        return true;
    }

    // Expected Results:
    // NULL: There is no user with this id
    // TRUE: Deleted
    // FALSE: ACTION NOT ALLOWED
    public Boolean deleteUserGuard(Integer id, Integer requestorId, Role requestorRole) {
        log.info("UserService.deleteUserGuard(" + id + ")");
        Optional<User>  existingUser = userRepository.findById(id);
        // Null check
        if (!existingUser.isPresent() || id == null) return null;

        User user = existingUser.get();

        // Authority Check
        if (requestorRole == Role.HELP_DESK && ( user.getRole() == Role.ADMIN || user.getRole() == Role.HELP_DESK )) return false;
        if (id == requestorId) return false;

        userRepository.deleteById(id.longValue());

        return true;
    }
}
