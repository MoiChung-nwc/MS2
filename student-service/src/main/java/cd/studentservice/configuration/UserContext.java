package cd.studentservice.configuration;

import lombok.Data;

import java.util.List;

@Data
public class UserContext {
    private Long userId;
    private String username;

    private Long studentId;

    private List<String> roles;
    private List<Long> schoolIds;
    private List<String> permissions;
    private List<Long> dataScopeSchoolIds;

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

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean canReadGrades() {
        // ADMIN can read all grades regardless of permissions
        if (isAdmin()) {
            return true;
        }
        return hasPermission("GRADE_READ");
    }

    public List<Long> getAccessibleSchoolIds() {
        if (isAdmin()) {
            return List.of(); // ADMIN has access to all schools
        }
        return dataScopeSchoolIds != null ? dataScopeSchoolIds : List.of();
    }

    public Long resolveTargetStudentId(Long requestedStudentId) {
        if (isStudent()) {
            return this.studentId;
        }

        return requestedStudentId;
    }
}
