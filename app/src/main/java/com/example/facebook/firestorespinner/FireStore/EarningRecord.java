package com.example.facebook.firestorespinner.FireStore;

import com.google.firebase.Timestamp;

import java.util.Date;

public class EarningRecord {

    private String sourceName;
    private int amount;
    private Date timestamp;

    public EarningRecord(){}

    public EarningRecord(String sourceName, int amount, Date timestamp) {

        this.sourceName = sourceName;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getAmount() {
        return amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
