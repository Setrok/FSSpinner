package com.example.facebook.firestorespinner.FireStore;

public class User {

    private String name;
    private String refFrom;
    private boolean hasRef;
    private String picture;
    private double score;

    public User(String name, String refFrom, boolean hasRef, String picture,double score) {
        this.name = name;
        this.refFrom = refFrom;
        this.hasRef = hasRef;
        this.picture = picture;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public String getRefFrom() {
        return refFrom;
    }

    public boolean isHasRef() {
        return hasRef;
    }

    public String getPicture() {
        return picture;
    }

    public User() {}


    
}
