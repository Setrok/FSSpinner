package com.example.facebook.firestorespinner.FireStore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Users {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    static final String TAG ="InfoAppFS";

    public static void addUser(String uID, final User user, final IhandleTransaction ihandleTransaction){

//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);

        final DocumentReference userDocRef = db.collection("users").document(uID);
        final DocumentReference counterDocRef = db.collection("users").document("commonData");
        final boolean[] userExists = new boolean[1];

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                DocumentSnapshot counterSnapshot = transaction.get(counterDocRef);

                long counter;
                boolean counterExists;


                if(counterSnapshot.exists()){
                    counter = counterSnapshot.getLong("counter");
                    counterExists = true;
                }
                else{
                    counter = 0;
                    Map<String, Object> data = new HashMap<>();
                    data.put("counter", counter+1);
                    counterExists = false;
                    transaction.set(counterDocRef,data);
                }

                if(!userSnapshot.exists()){
                    //TODO Change to false
                    userExists[0] = true;
                    user.setCounter(counter+1);
                    transaction.set(userDocRef,user);
                    if(counterExists)
                        transaction.update(counterDocRef,"counter",counter+1);
                } else{
                    userExists[0] = true;
                    transaction.update(userDocRef,"picture",user.getPicture());
                    transaction.update(counterDocRef,"counter",counter);
                }
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
                if(userExists[0])
                    ihandleTransaction.loadActivity();
                else{
                    ihandleTransaction.showRefFragment();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                    ihandleTransaction.displayError("Error uploading data");
                }
            });

    }

    public static void getUserData(String uid, final IsideNavBar isideNavBar){

        final DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                if(user!=null) {
                    Log.i(TAG, "picture from fs is:" + user.getPicture());
                    Log.i(TAG, "name from fs is:" + user.getName());

                    isideNavBar.setUserImage(user.getPicture());
                    isideNavBar.setUserName(user.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isideNavBar.navBarDataError(e.getLocalizedMessage());
            }
        });

    }

    public static void getUserCounter(String uid, final IhandlCounter ihandlCounter){

        final DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                if(user!=null) {
//                    Log.i(TAG, "picture from fs is:" + user.getPicture());

                    long counter = documentSnapshot.getLong("counter");
                    ihandlCounter.getCounter(counter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ihandlCounter.displayError( e);
            }
        });

    }

    public static void checkIfRefExists(final String uid, final long counter, final IhandleTransaction ihandleTransaction){
        db.collection("users")
                .whereEqualTo("counter", counter)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty())
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if( (counter == document.getLong("counter"))){
                                    ihandleTransaction.displayError("Wrong code");
                                } else {
                                    addReferal(uid, document.getId(),ihandleTransaction);
                                    break;
                                }
                            } else {
                                ihandleTransaction.displayError("Wrong code");
                            }
                            Log.d(TAG, "Emty? "+ task.getResult().isEmpty());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void checkIfRefExists2(final String uid, final long counter){
        db.collection("users")
                .whereEqualTo("counter", counter)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if( (counter == document.getLong("counter"))){
                                    //ihandleTransaction.displayError("Wrong code");
                                    Log.d(TAG, "Wrong code ", task.getException());
                                } else {
                                    Log.d(TAG, "Ok");
                                    break;
                                }
                            }
                            Log.d(TAG, "Emty? "+ task.getResult().isEmpty());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void addReferal(String uid, String refFrom, final IhandleTransaction ihandleTransaction){

        DocumentReference refRef = db.collection("users").document(uid);
        refRef
                .update("refFrom", refFrom)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        ihandleTransaction.loadActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ihandleTransaction.displayError("Error setting referal");
                    }
                });

    }

    public interface IhandleTransaction{

        void loadActivity();

        void displayError(String error);

        void showRefFragment();

    }

    public interface IhandlCounter{

        void getCounter(long counter);

        void displayError(Exception e);
    }

    public interface IsideNavBar{

        void setUserImage(String img);

        void setUserName(String name);

        void navBarDataError(String error);

    }

}
