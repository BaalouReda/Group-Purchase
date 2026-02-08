package com.kata.grouppurchase.enums;

public enum Status {
    PENDING("PENDING"),
    FULL("FULL"),
    FINALIZED("FINALIZED"),
    CANCELLED("CANCELLED");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Status getStatus(String name) {
        for (Status status : Status.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }
}
