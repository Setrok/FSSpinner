package com.example.facebook.firestorespinner.FireStore;

import com.google.firebase.Timestamp;

public class EarningRecord {

    private String sourceName;
    private double amount;
    private Timestamp timestamp;

    public EarningRecord(String sourceName, double amount, Timestamp timestamp) {

        this.sourceName = sourceName;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getSourceName() {
        return sourceName;
    }

    public double getAmount() {
        return amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
