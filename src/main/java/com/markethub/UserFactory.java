package com.markethub;

import com.markethub.model.User;

public class UserFactory {
    public static User createUser(String role, String fullName, String email, String encodedPassword, String storeName, String cnic) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role.toUpperCase());

        if ("VENDOR".equalsIgnoreCase(role)) {
            user.setStoreName(storeName);
            user.setCnic(cnic);
        }
        return user;
    }
}