package com.school.school.exception;

public class UnauthorizedRoleActionException extends RuntimeException {
    public UnauthorizedRoleActionException(String message) {
        super(message);
    }
}
