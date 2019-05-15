package com.ucsd.tryclubs.Model;

public class FollowingClubs {

    public String id;
    public String club_name;

    public FollowingClubs() {
    }

    public FollowingClubs(String id, String club_name) {
        this.id = id;
        this.club_name = club_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClub_name() {
        return club_name;
    }

    public void setClub_name(String club_name) {
        this.club_name = club_name;
    }
}
