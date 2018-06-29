package com.example.facebook.firestorespinner.FireStore;

import com.google.firebase.Timestamp;

import java.util.Date;

public class WithdrawRecord {

    private double paytmNumber;
    private double amount;
    private Timestamp timestamp;

    public WithdrawRecord(long paytmNumber, double amount, Timestamp time) {

        this.paytmNumber = paytmNumber;
        this.amount = amount;
        this.timestamp = time;
    }

    public double getPaytmNumber() {
        return paytmNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
