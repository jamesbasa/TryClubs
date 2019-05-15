package com.ucsd.tryclubs.Model;

public class ClubMembers {

    public String names;
    public String email;

    public ClubMembers() {

    }

    public ClubMembers(String names, String email) {
        this.names = names;
        this.email = email;
    }

    public String getNames() {
        return names;
    }

    public String getEmail() {
        return email;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
