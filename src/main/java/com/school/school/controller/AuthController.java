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

    // @Autowired AuthenticationManager authenticationManager;

    // @PostMapping("/authenticate")
    // public AuthenticationResponse createToken(@RequestBody AuthenticationRequest request) {
    //     log.info("createToken(-)");

    //     try {
    //         authenticationManager.authenticate(
    //             new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    //         );

    //         UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    //         String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

    //         return new AuthenticationResponse(jwtToken);

    //     } catch (BadCredentialsException ex) {
    //         // throw new BadCredentialsException("Invalid username or password");
    //         return new AuthenticationResponse("Wrong Credentials");
    //     }
    // }

    // @PostMapping("/authenticate")
    // public ResponseEntity<AuthenticationResponse> createToken(@RequestBody AuthenticationRequest request) {
    //     log.info("createToken(-)");
    //     // Authenticate the user
    //     // userDetailsService.loadUserByUsername(request.getUsername());
    //     userDetailsService.authenticate(request.getUsername(), request.getPassword());

    //     // Generate the token
    //     String jwtToken = jwtUtil.generateToken(request.getUsername());

    //     // return new AuthenticationResponse(jwtToken);
    //     return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    // }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createToken(@RequestBody AuthenticationRequest request) {
        log.info("createToken(-)");

        try {
            // Authenticate the user
        // userDetailsService.loadUserByUsername(request.getUsername());
        boolean auth = userService.authenticate(request.getUsername(), request.getPassword());

        // block if auth failed
        if (auth == false) {
            throw new BadCredentialsException("Wrong Credentials");
        }

        // Generate the token
        String jwtToken = jwtUtil.generateToken(request.getUsername());

        // return new AuthenticationResponse(jwtToken);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(new AuthenticationResponse());
        }
    }


}