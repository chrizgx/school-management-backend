package com.school.school.entity;

public enum Role {
    ADMIN("ADMIN"),
    HELP_DESK("HELP_DESK"),
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }

    public static Role getFromString(String roleString) {
        for (Role role : Role.values()) {
            if (role.displayName.equalsIgnoreCase(roleString)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role found for string: " + roleString);
    }
}