package org.example.security;

public interface Authentication {
    long getId();
    String getName();
    boolean isAnonymous();
    default String[] getRoles() {
        return new String[]{};
    }
    default boolean hasRole(String role) {
        for (String r : getRoles()) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
