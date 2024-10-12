package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize; 

// import jakarta.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') hasRole('HELP_DESK')")
    public Iterable<User> getMethodName() {

        return userService.getAllUsers();
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
}
