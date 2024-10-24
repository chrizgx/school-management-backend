package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.dto.user.CreateUserDTO;
import com.school.school.dto.user.UserDTO;
import com.school.school.entity.Role;
import com.school.school.service.CustomUserDetailsService;
import com.school.school.service.UserService;

import com.school.school.response.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.validation.annotation.Validated;

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
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public Iterable<User> getAllUsers() {

        return userDetailsService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserDTO>> getUser(@PathVariable("id") Integer id) {
        UserDTO user = userService.getUser(id);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>("User with ID " + id + "does not exist.", false));
        return ResponseEntity.ok(new ResponseWrapper<>(user, true));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<ResponseWrapper<UserDTO>> createUser(@RequestBody CreateUserDTO user, @RequestAttribute("id") Integer requestorId, @RequestAttribute("role") Role role) {
        log.info("POST:/api/users createUser(-)");
        UserDTO createdUser = userService.createUserGuard(user, requestorId, role);
        if (createdUser == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>(user.getEmail() + " already exists.", false));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(createdUser, true));
    }
    

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<ResponseWrapper<UserDTO>> updateUser(@PathVariable("id") Integer id, @RequestBody User user, @RequestAttribute("id") Integer requestorId, @RequestAttribute("role") Role requestorRole) {
        log.info("PUT:/api/users" + id + "(-)");
        // Role requestorRole = Role.getFromString(requestorRoleString);
        UserDTO updatedUser = userService.updateUserGuard(id, user, requestorId, requestorRole);

        if (updatedUser != null) return ResponseEntity.ok(new ResponseWrapper<>(updatedUser, true));
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundWrapper<>("user", id));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<ResponseWrapper<Void>> updatePassword(@PathVariable("id") Integer id, @RequestBody String password, @RequestAttribute("id") Integer requestorId, @RequestAttribute("role") Role requestorRole) {
        log.info("PUT:/api/users/" + id + "/password updatePassword(-)");

        Boolean updated = userService.updatePasswordGuard(id, password, requestorId, requestorRole);

        if (updated == true) return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseWrapper<>("User " + id + " password updated successfully", true));
        if (updated == false) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>("HELP_DESK users can not update the password of other 'ADMIN' or 'HELP_DESK' accounts", false));
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundWrapper<>("user", id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HELP_DESK')")
    public ResponseEntity<ResponseWrapper<Void>> deleteUser(@PathVariable("id") Integer id, @RequestAttribute("id") Integer requestorId, @RequestAttribute("role") Role requestorRole) {
        log.info("DEL:/api/users" + id + "(-)");

        Boolean deleted = userService.deleteUserGuard(id, requestorId, requestorRole);

        if (deleted == true) return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseWrapper<>("User #" + id + " deleted successfully", true));
        if (deleted == false) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>("'HELP_DESK' users can not delete other 'HELP_DESK' or 'ADMIN' users. No user can delete their own account.", false));
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundWrapper<>("user", id));
    }
    
}
