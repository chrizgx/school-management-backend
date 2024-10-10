package com.school.school.service;


import com.school.school.entity.Role;
import com.school.school.entity.User;
import com.school.school.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("loadUserByUsername(-)");
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, new ArrayList<>());
        } catch (UsernameNotFoundException ex) {
            return null;
        }
    }

    public Boolean authenticate (String mail, String password) {
        log.info("authenticate(-)");
        UserDetails user = loadUserByUsername(mail);

        if (user.getPassword() == password) {
            return true;
        }

        throw new UsernameNotFoundException("Wrong Credentials");
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // user.setEnabled(true);
        return userRepository.save(user);
    }
}