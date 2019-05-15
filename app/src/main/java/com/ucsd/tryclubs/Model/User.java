package com.ucsd.tryclubs.Model;

public class User {

    public String username;
    public String email;
    public String uid;
    public String following_clubs;
    public String following_events;

    public User() {
    }

    public User(String username, String email, String uid, String following_clubs, String following_events) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.following_clubs = following_clubs;
        this.following_events = following_events;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFollowing_clubs() {
        return following_clubs;
    }

    public void setFollowing_clubs(String following_clubs) {
        this.following_clubs = following_clubs;
    }

    public String getFollowing_events() {
        return following_events;
    }

    public void setFollowing_events(String following_events) {
        this.following_events = following_events;
    }
}
