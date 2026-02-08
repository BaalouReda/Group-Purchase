package com.kata.grouppurchase.enums;

public enum Role {
    CUSTOMER("CUSTOMER"),
    ADMIN("ADMIN");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Role getRole(String name) {
        for (Role role : Role.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }
}
