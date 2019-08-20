package com.kitecoin.free.quizmania.FireStore;

import com.google.firebase.Timestamp;

import java.util.Date;

public class WithdrawRecord {

    private String paytmNumber;
    private int amount;
    private Date timestamp;

    public WithdrawRecord(){};

    public WithdrawRecord(String paytmNumber, int amount, Date timestamp) {

        this.paytmNumber = paytmNumber;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getPaytmNumber() {
        return paytmNumber;
    }

    public int getAmount() {
        return amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
