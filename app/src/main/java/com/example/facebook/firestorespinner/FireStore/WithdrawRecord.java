package com.example.facebook.firestorespinner.FireStore;

import com.google.firebase.Timestamp;

import java.util.Date;

public class WithdrawRecord {

    private long paytmNumber;
    private int amount;
    private Date timestamp;

    public WithdrawRecord(){};

    public WithdrawRecord(long paytmNumber, int amount, Date timestamp) {

        this.paytmNumber = paytmNumber;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public long getPaytmNumber() {
        return paytmNumber;
    }

    public int getAmount() {
        return amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
