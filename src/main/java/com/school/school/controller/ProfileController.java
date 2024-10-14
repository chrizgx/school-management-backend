package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.entity.Role;
import com.school.school.service.CustomUserDetailsService;
import com.school.school.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@Slf4j
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<User> getProfile(@RequestAttribute("id") Integer id) {
        try {
            log.info("GET:/api/profile" + "getProfile(-)");
            User user = userService.getUser(id);
            
            if (user == null) return ResponseEntity.notFound().build();
            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody String password, @RequestBody String newPassword, @RequestAttribute("id") Integer id) {
        try {
            log.info("PUT:/api/profile/password updatePassword(-)");
            Boolean updated = userService.updatePasswordGuard(id, password, newPassword);

            if (updated == true) return ResponseEntity.accepted().build();
            if (updated == false) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.info("Error: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
}
