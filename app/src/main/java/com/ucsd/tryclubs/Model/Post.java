package com.ucsd.tryclubs.Model;

public class Post {
    private String ename;
    private String hosts;
    private String location;
    private String date;
    private String time;
    private String description;

    private long sort_date;

    public Post() {
    } // needed for firebase?

    public Post(String ename, String hosts, String location, String date, String time, String description, long sort_date) {
        this.ename = ename;
        this.hosts = hosts;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.sort_date = sort_date;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSort_date() {
        return sort_date;
    }

    public void setSort_date(long sort_date) {
        this.sort_date = sort_date;
    }
}