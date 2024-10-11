package com.school.school.security.aspect;

import com.school.school.security.annotations.*;

import com.school.school.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class PreAuthorizeAspect {

    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(com.school.school.security.annotations.PreAuthorize)")
    public Object handleAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("handleAuthorization(-)");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        PreAuthorize customAuthorize = signature.getMethod().getAnnotation(PreAuthorize.class);
        String[] allowedRoles = customAuthorize.roles();

        // Get JWT from request header
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authorizationHeader = request.getHeader("Authorization");

        // Extract token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            // Extract roles from JWT
            String role = (String) jwtUtil.extractClaims(token).get("role");

            // Check if the user has any of the allowed roles
            if (role != null && checkRoles(role, allowedRoles)) {
                return joinPoint.proceed(); // Allow access
            }
        }

        // If not authorized
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
    }

    private boolean checkRoles(String userRole, String[] allowedRoles) {
        for (String allowedRole : allowedRoles) {
            if (userRole.equals(allowedRole)) {
                return true;
            }
        }
        return false;
    }
}

