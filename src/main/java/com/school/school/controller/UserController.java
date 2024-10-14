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
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<User> createUser(@RequestBody User user, @RequestAttribute("role") Role role) {
        try {
            log.info("POST:/api/user createUser(-)");
            User createdUser = userService.createUserGuard(user, role);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.info("error: " + e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer id, @RequestAttribute("id") Integer requestorId, @RequestAttribute("role") Role requestorRole) {
        try {
            log.info("DEL:/api/user/" + id + "(-)");

            Boolean deleted = userService.deleteUserGuard(id, requestorId, requestorRole);

            if (deleted == null) return ResponseEntity.notFound().build();
            if (deleted == false) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            if (deleted == true) return ResponseEntity.noContent().build();

            throw new Exception("'Deleted' value is not null, false or true");
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
