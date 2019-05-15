package com.ucsd.tryclubs.Model;

public class FollowingClubs {

    public String club_id;
    public String club_name;

    public FollowingClubs() {
    }

    public FollowingClubs(String club_id, String club_name) {
        this.club_id = club_id;
        this.club_name = club_name;
    }

    public String getClub_id() {
        return club_id;
    }

    public void setClub_id(String club_id) {
        this.club_id = club_id;
    }

    public String getClub_name() {
        return club_name;
    }

    public void setClub_name(String club_name) {
        this.club_name = club_name;
    }
}
