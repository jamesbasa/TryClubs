package com.ucsd.tryclubs.Model;

public class User {

    public String username;
    public String email;
    public String userType;

    public User() {
    }

    public User(String username, String email, String userType) {
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
