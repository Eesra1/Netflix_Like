package com.jstream.service;

import com.jstream.model.User;

public class SessionManager {

    private static User currentUser;

    public static void setCurrentUser(User u) {
        currentUser = u;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equals("ADMIN");
    }

    public static void logout() {
        currentUser = null;
    }
}