package com.example.facebook.firestorespinner.FireStore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class ScoreManager {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    static final String TAG ="InfoAppFS";

    public static void deductScore(final double paytmNumber, final String uid, final double amount, final IreedemActivityHandler ireedemActivityHandler){


        final DocumentReference userDocRef = db.collection("users").document(uid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                try{
                DocumentSnapshot snapshot = transaction.get(userDocRef);

                double score = snapshot.getDouble("score");
                if (score >= 1000 && score >= amount) {

                    Map<String,Object> withdrawMap = new HashMap();
                    withdrawMap.put("paytmNumber",paytmNumber);
                    withdrawMap.put("amount",amount);
                    withdrawMap.put("time", FieldValue.serverTimestamp());

                    DocumentSnapshot withdrawSnapshot = transaction.get(userDocRef);
                    final DocumentReference withdrawRef = db.collection("withdraw").document(uid).collection("withdrawRecords").document();
                    transaction.update(userDocRef, "score", score-amount);
                    transaction.set(withdrawRef,withdrawMap);

                }
                } catch (Exception e){
                    ireedemActivityHandler.displayMessage("Error uploading data");
                }
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
                ireedemActivityHandler.displayMessage("Data is sent");
                ireedemActivityHandler.showProgressBar(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                ireedemActivityHandler.showProgressBar(false);
                if(e instanceof FirebaseFirestoreException){
                    ireedemActivityHandler.displayMessage("Not enough scores");
                } else
                ireedemActivityHandler.displayMessage("Error Sending data");
            }
        });
    }

    public static void addScore(final String uid, final double amount, final String sourceName , final boolean addToHistory){

        final DocumentReference userDocRef = db.collection("users").document(uid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                try{
                    DocumentSnapshot snapshot = transaction.get(userDocRef);

                    double score = snapshot.getDouble("score");

                    if(addToHistory) {
                        Map<String, Object> earningMap = new HashMap();
                        earningMap.put("sourceName", sourceName);
                        earningMap.put("amount", amount);
                        earningMap.put("timestamp", FieldValue.serverTimestamp());

                        DocumentSnapshot earningsSnapshot = transaction.get(userDocRef);
                        final DocumentReference earningRef = db.collection("earnings").document(uid).collection("earningRecords").document();
                        transaction.set(earningRef,earningMap);
                    }

                        transaction.update(userDocRef, "score", score+amount);

                } catch (Exception e){
                    //ireedemActivityHandler.displayMessage("Error uploading data");
                }
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
//                ireedemActivityHandler.displayMessage("Data is sent");
//                ireedemActivityHandler.showProgressBar(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
//                ireedemActivityHandler.showProgressBar(false);
            }
        });


    }

    public interface IreedemActivityHandler{

        void showProgressBar(boolean b);

        void displayMessage(String error);

    }

}
