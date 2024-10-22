package com.school.school.security.aspects;

import com.school.school.security.annotations.Sanitize;
import com.school.school.exception.InvalidInputException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class SanitizeAspect {
    
    @Before("execution(* com.school.school.dto..*(..))")
    public void sanitizeFields(JoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        Field[] fields = target.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(Sanitize.class)) {
                field.setAccessible(true);
                String value = (String) field.get(target);
                String sanitizedValue = sanitize(value);
                field.set(target, sanitizedValue);
            }
        }
    }

    // Sanitization logic
    private String sanitize(String input) {
        if (input == null) return null;

        // Check for illegal characters
        if (!input.matches("[\\w\\s@.]+")) {
            throw new InvalidInputException("Input contains illegal characters: " + input);
        }

        return input.trim();
    }
}
