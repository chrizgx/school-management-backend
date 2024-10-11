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
}
