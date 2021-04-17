package com.jango.user.enumeration;

public enum Roles {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MODERATOR("ROLE_MODERATOR");
    
    private final String string;
    
    Roles(final String string) {
        this.string = string;
    }
    
    public String toString() {
        return string;
    }
}
