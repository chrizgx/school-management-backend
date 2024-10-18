package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.response.*;
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
    public ResponseEntity<ResponseWrapper<User>> getProfile(@RequestAttribute("id") Integer id) {
        log.info("GET:/api/profile" + "getProfile(-)");
        User user = userService.getUser(id);
        
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundWrapper<>("user", id));
        else return ResponseEntity.ok(new ResponseWrapper<>(user, true));
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseWrapper<Void>> updatePassword(@RequestBody UpdatePasswordRequest body, @RequestAttribute("id") Integer id) {
        log.info("PUT:/api/profile/password updatePassword(-)");
        if (body.password == null || body.newPassword == null) return ResponseEntity.badRequest().build();
        Boolean updated = userService.updatePasswordGuard(id, body.password, body.newPassword);

        if (updated == true) return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseWrapper<>("Password updated", true));
        if (updated == false) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>("Authentication failed", false));

        return ResponseEntity.notFound().build();
    }

    public static class UpdatePasswordRequest {
        public String password;
        public String newPassword;
    }
    
    
}
