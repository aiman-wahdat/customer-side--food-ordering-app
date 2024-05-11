package com.example.smdproject;

public class current_user_singleton {
    private static current_user_singleton instance;
    private String userId;

    private current_user_singleton() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized current_user_singleton getInstance() {
        if (instance == null) {
            instance = new current_user_singleton();
        }
        return instance;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void logout() {
        // Clear current user data or reset the instance as needed
        instance = null;
    }


}
