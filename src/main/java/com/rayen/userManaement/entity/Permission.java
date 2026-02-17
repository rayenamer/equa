package com.rayen.userManaement.entity;

public enum Permission {
    VIEW_LOGS("View system logs"),
    AUDIT("Perform audits"),
    MANAGE_USERS("Manage users"),
    MONITOR_TRANSACTIONS("Monitor blockchain transactions"),
    VIEW_REPORTS("View financial reports"),
    APPROVE_LOANS("Approve loan requests");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
