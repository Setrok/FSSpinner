package com.kitecoin.free.quizmania.FireStore;

import java.util.Date;

public class User {

    private String name;
    private String refFrom;
    private String picture;
    private long score;
    private long counter;

    private Date prevTimestamp;

    private Date currentTimestamp;

    private long spins;

    private long quizTries;


    public User(String name, String refFrom, String picture,long score,long counter,long spins,long quizTries) {
        this.name = name;
        this.refFrom = refFrom;
        this.picture = picture;
        this.score = score;
        this.counter = counter;
        this.spins = spins;
        this.quizTries = quizTries;
    }

    public long getSpins() {
        return spins;
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

    public long getQuizTries() {
        return quizTries;
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

    public Date getPrevTimestamp() {
        return prevTimestamp;
    }

    public Date getCurrentTimestamp() {
        return currentTimestamp;
    }

    public User() {}



}
