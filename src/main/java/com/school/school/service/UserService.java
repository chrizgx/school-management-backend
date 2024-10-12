package com.school.school.service;

import com.school.school.entity.Role;
import com.school.school.entity.User;
import com.school.school.repository.UserRepository;
import com.school.school.security.JwtUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private UserDetailsService userDetailsService; // Inject the UserDetailsService

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

    // 
    public User updateUserGuard(Integer id, User user, Integer requestorId, Role requestorRole) {
        Optional<User> existingUser = userRepository.findById(id);

        if (!existingUser.isPresent()) return null;
        User userToUpdate = existingUser.get(); 

        // Authentication Logic starts here
        // Admin can edit everyone's profile and ALL attributes
        if (requestorRole == Role.ADMIN) return updateUser(userToUpdate.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(), user.isEnabled());

        // Help desk can edit all role's profile except Admin's and Help Desks (including themselves)
        // Help desk CANNOT edit user role.
        if (requestorRole == Role.HELP_DESK && userToUpdate.getRole() != Role.ADMIN && userToUpdate.getRole() != Role.HELP_DESK)
            return updateUser(userToUpdate.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), null, user.isEnabled());

        log.info("updateUserGuard(- ID# " + requestorId + " illegally tried to update profile ID#" + user.getId());
        return null;

    }

    public User updateUser(Long id, String firstName, String lastName, String email, Role role, Boolean enabled) {
        Optional<User> existingUser = userRepository.findById(id);

        if (!existingUser.isPresent()) return null;

        User user = existingUser.get();

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (role != null) user.setRole(role);
        if (enabled != null) user.setEnabled(enabled);

        return userRepository.save(user);
    }
}
