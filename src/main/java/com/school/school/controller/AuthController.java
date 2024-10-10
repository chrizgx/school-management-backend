package com.school.school.controller;


import com.school.school.security.AuthenticationRequest;
import com.school.school.security.AuthenticationResponse;
import com.school.school.security.JwtUtil;
import com.school.school.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired 
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createToken(@RequestBody AuthenticationRequest request) {
        log.info("createToken(-)");

        try {
            // Authenticate the user
        boolean auth = userService.authenticate(request.getUsername(), request.getPassword());

        // block if auth failed
        if (auth == false) {
            throw new BadCredentialsException("Wrong Credentials");
        }

        // Generate the token
        // String jwtToken = jwtUtil.generateToken(request.getUsername());
        String jwtToken = userService.generateToken(request.getUsername());

        // return new AuthenticationResponse(jwtToken);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(new AuthenticationResponse());
        }
    }


}