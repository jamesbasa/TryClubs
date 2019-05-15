package com.ucsd.tryclubs.Model;

public class ClubMembers {

    public String name;
    public String email;

    public ClubMembers() {

    }

    public ClubMembers(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
