package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.entity.Role;
import com.school.school.service.CustomUserDetailsService;
import com.school.school.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize; 

// import jakarta.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public Iterable<User> getAllUsers() {

        return userDetailsService.getAllUsers();
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userDetailsService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<User> updateUser(@PathVariable("id") Integer id, @RequestBody User user, @RequestAttribute("id") Integer requestorId, @RequestAttribute("role") Role requestorRole) {
        try {
            log.info("PUT:/api/user/" + id + "(-)");
            // Role requestorRole = Role.getFromString(requestorRoleString);
            User updatedUser = userService.updateUserGuard(id, user, requestorId, requestorRole);

            if (updatedUser != null) {
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }

            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.info("Error: " + e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
