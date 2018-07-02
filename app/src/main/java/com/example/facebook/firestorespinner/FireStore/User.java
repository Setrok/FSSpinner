package com.example.facebook.firestorespinner.FireStore;

public class User {

    private String name;
    private String refFrom;
    private boolean hasRef;
    private String picture;

    private long score;

    private long counter;

    public User(String name, String refFrom, boolean hasRef, String picture,long score,long counter) {
        this.name = name;
        this.refFrom = refFrom;
        this.hasRef = hasRef;
        this.picture = picture;
        this.score = score;
        this.counter = counter;
    }

    public String getName() {
        return name;
    }

    public long getScore() {
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

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public User() {}



}
