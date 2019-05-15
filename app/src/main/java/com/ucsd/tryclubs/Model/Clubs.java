package com.ucsd.tryclubs.Model;

public class Clubs {

    public String id;
    public String club_name;
    public String purpose;

    public Clubs() {
    }

    public Clubs(String id, String club_name, String purpose) {
        this.id = id;
        this.club_name = club_name;
        this.purpose = purpose;
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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
