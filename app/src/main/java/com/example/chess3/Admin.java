package com.example.chess3;

import java.util.List;

public class Admin extends User {
    private String adminId;        // Unique ID for admin
    private String role;           // Admin role (e.g., "super_admin", "moderator")
    private List<String> permissions; // List of permissions
    private List<User> managedUsers;   // List of users managed by this admin

    // Constructor
    public Admin(String email, String password, String username, int lvl, String adminId, String role) {
        super(email, password, username, lvl);  // Call parent constructor
        this.adminId = adminId;
        this.role = role;
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<User> getManagedUsers() {
        return managedUsers;
    }

    public void setManagedUsers(List<User> managedUsers) {
        this.managedUsers = managedUsers;
    }
}
