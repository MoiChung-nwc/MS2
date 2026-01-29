package com.savvy.gradeservice.config;

import lombok.Data;

import java.util.List;

@Data
public class UserContext {
    private Long userId;
    private String username;
    private List<String> roles;
    private List<Long> schoolIds;

    private static final ThreadLocal<UserContext> currentUser = new ThreadLocal<>();

    public static void set(UserContext context) {
        currentUser.set(context);
    }

    public static UserContext get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean isStudent() {
        return hasRole("STUDENT");
    }

    public boolean isManager() {
        return hasRole("SCHOOL_MANAGER");
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean hasSchoolAccess(Long schoolId) {
        if (isAdmin()) {
            return true;
        }
        return schoolIds != null && schoolIds.contains(schoolId);
    }
}
