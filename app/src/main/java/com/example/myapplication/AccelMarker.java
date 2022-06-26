package com.example.myapplication;

public class AccelMarker {

    String User;
    Float acc;
    double loc_lat,loc_long;
    Long time;
    Float speed;

    public AccelMarker(String user, Float acc, double loc_lat, double loc_long, Long time, Float speed) {
        User = user;
        this.acc = acc;
        this.loc_lat = loc_lat;
        this.loc_long = loc_long;
        this.time = time;
        this.speed = speed;
    }

    public AccelMarker(){};

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getAcc() {
        return acc;
    }

    public void setAcc(Float acc) {
        this.acc = acc;
    }

    public double getLoc_lat() {
        return loc_lat;
    }

    public void setLoc_lat(double loc_lat) {
        this.loc_lat = loc_lat;
    }

    public double getLoc_long() {
        return loc_long;
    }

    public void setLoc_long(double loc_long) {
        this.loc_long = loc_long;
    }
}
