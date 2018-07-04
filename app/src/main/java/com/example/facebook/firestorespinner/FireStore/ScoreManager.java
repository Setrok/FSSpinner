package com.example.facebook.firestorespinner.FireStore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.facebook.firestorespinner.utils.NetworkConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            ireedemActivityHandler.displayMessage("No Internet access");
//            return;
//        }

        final DocumentReference userDocRef = db.collection("users").document(uid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                try{
                DocumentSnapshot snapshot = transaction.get(userDocRef);

                double score = snapshot.getLong("score");
                if (score >= 1000 && score >= amount) {

                    Map<String,Object> withdrawMap = new HashMap();
                    withdrawMap.put("paytmNumber",paytmNumber);
                    withdrawMap.put("amount",amount);
                    withdrawMap.put("timestamp", FieldValue.serverTimestamp());

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
                ireedemActivityHandler.displayMessage("Data is successfully sent for processing");
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

    public static void addScore(final String uid, final double amount, final String sourceName,
                                final boolean addToHistory, final boolean addToReferal, final IscoreMessage iscoreMessage){

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            iscoreMessage.displayError("No Internet access");
//            return;
//        }

        final DocumentReference userDocRef = db.collection("users").document(uid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                try{
                    DocumentSnapshot snapshot = transaction.get(userDocRef);

                    double score = snapshot.getLong("score");
                    String refFrom = snapshot.getString("refFrom");

                    if(refFrom.length()>0 && addToReferal){
                        addScore(refFrom,amount/10,"Referal",true,false,iscoreMessage);
                    }

                    if(addToHistory) {
                        Map<String, Object> earningMap = new HashMap();
                        earningMap.put("sourceName", sourceName);
                        earningMap.put("amount", amount);
                        earningMap.put("timestamp", FieldValue.serverTimestamp());

//                        DocumentSnapshot earningsSnapshot = transaction.get(userDocRef);
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
                iscoreMessage.scoreAddSuccess();
//                ireedemActivityHandler.displayMessage("Data is sent");
//                ireedemActivityHandler.showProgressBar(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                iscoreMessage.displayError("Error adding scores");
//                ireedemActivityHandler.showProgressBar(false);
            }
        });


    }

    public static void getScore(String uid, final IscoreDisplay iscoreDisplay){

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            iscoreDisplay.displayError("No Internet access");
//            return;
//        }

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        long score  = document.getLong("score");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getLong("score"));
                        iscoreDisplay.setScore(score);
                    } else {
                        iscoreDisplay.displayError("Error uploading data");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    iscoreDisplay.displayError("Error uploading data");
                }
            }
        });

    }

    public interface IreedemActivityHandler{

        void showProgressBar(boolean b);

        void displayMessage(String error);

    }

    public interface IscoreDisplay{

        void setScore(long i);

        void displayError(String error);

    }

    public interface IscoreMessage {

        void displayError(String error);

        void scoreAddSuccess();

    }

}
