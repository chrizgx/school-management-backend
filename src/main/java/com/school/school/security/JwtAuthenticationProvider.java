// package com.school.school.security;

// import com.school.school.entity.Role;

// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;

// import java.util.Collection;
// import java.util.List;
// import java.util.stream.Collectors;

// @Component
// @Slf4j
// public class JwtAuthenticationProvider implements AuthenticationProvider {

//     private final JwtUtil jwtUtil;

//     public JwtAuthenticationProvider(JwtUtil jwtUtil) {
//         this.jwtUtil = jwtUtil;
//     }

//     @Override
//     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//         String token = (String) authentication.getCredentials();
//         String username = jwtUtil.extractUsername(token);

//         // Extract roles from the JWT (assuming you store roles in a claim)
//         Role role = jwtUtil.extractClaims(token).get("roles", Role.class);

//         // Create a new authentication token with the extracted roles
//         UsernamePasswordAuthenticationToken authenticationToken = 
//                 new UsernamePasswordAuthenticationToken(username, role);

//         // Set the authentication in the SecurityContextHolder
//         SecurityContextHolder.getContext().setAuthentication(authenticationToken);

//         log.info("Provider.generateToken(-)");
//         log.info(role.toString());
//         log.info(username);

//         return authenticationToken;
//     }

//     @Override
//     public boolean supports(Class<?> authentication) {
//         return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
//     }
// }
package com.school.school.security;

import com.school.school.entity.Role;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        String username = jwtUtil.extractUsername(token);

        // Extract the role from the JWT (assuming you store roles in a claim)
        String role = jwtUtil.extractClaims(token).get("role", String.class); // Correct claim name and type

        // Create a collection with a single GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)); // Add "ROLE_" prefix

        // Create a new authentication token with the extracted role
        UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, null, authorities); // Remove role from second argument

        // Set the authentication in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("Provider.generateToken(-)");
        log.info(role);
        log.info(username);

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
