package com.ucsd.tryclubs.Model;

public class FollowingClubs {

    public String clubName;
    public String clubID;

    public FollowingClubs() {
    }

    public FollowingClubs(String clubName, String clubID) {
        this.clubName = clubName;
        this.clubID = clubID;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }
}
