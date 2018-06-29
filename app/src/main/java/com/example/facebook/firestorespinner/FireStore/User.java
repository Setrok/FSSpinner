package com.example.facebook.firestorespinner.FireStore;

public class User {

    private String name;
    private String refFrom;
    private boolean hasRef;
    private String picture;

    public User(String name, String refFrom, boolean hasRef, String picture) {
        this.name = name;
        this.refFrom = refFrom;
        this.hasRef = hasRef;
        this.picture = picture;
    }

    public String getName() {
        return name;
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
