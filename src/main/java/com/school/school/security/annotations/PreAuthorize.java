package com.school.school.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) // Apply to controller methods
public @interface PreAuthorize {
    String[] roles() default {}; // Specify allowed roles

}