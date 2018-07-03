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

    public static void addUser(String uID, final User user, final IhandleTransaction ihandleTransaction) {

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            ihandleTransaction.displayError("No Internet access");
//            return;
//        }

        final DocumentReference userDocRef = db.collection("users").document(uID);
        final DocumentReference counterDocRef = db.collection("users").document("commonData");
        final boolean[] userExists = new boolean[1];
        final long[] spins = new long[1];

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                DocumentSnapshot counterSnapshot = transaction.get(counterDocRef);

                long counter;
                boolean counterExists;


                if(userSnapshot.exists()){
                    spins[0] = userSnapshot.getLong("spins");
                }
                else spins[0]=user.getSpins();

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
                    userExists[0] = false;
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
                ihandleTransaction.showProgressBar(false);

                if(userExists[0]) {
                    ihandleTransaction.getSpinsLeft(spins[0],false);
                }
                else{
                    ihandleTransaction.getSpinsLeft(spins[0],true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ihandleTransaction.showProgressBar(false);
                    Log.w(TAG, "Transaction failure.", e);
                    ihandleTransaction.displayError("Error uploading data");
                }
            });

    }

    public static void getUserData(String uid, final IsideNavBar isideNavBar){

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            isideNavBar.navBarDataError("No Internet access");
//            return;
//        }

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

    public static void setUserSpinCounter(String uid, final int spins, final IsetSpinCounter isetSpinCounter){

        DocumentReference refRef = db.collection("users").document(uid);
        refRef
                .update("spins", spins)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        isetSpinCounter.setCounterSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isetSpinCounter.setCounterSuccess(false);
                    }
                });
    }

    public static void getUserCounter(String uid, final IhandlCounter ihandlCounter){

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            ihandlCounter.displayError("No Internet access");
//            return;
//        }

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
                ihandlCounter.displayError("Error uploading data");
            }
        });

    }


    public static void checkIfRefExists(final String uid, final long counter, final IhandleTransaction ihandleTransaction){

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            ihandleTransaction.displayError("No Internet access");
//            return;
//        }

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
//                                if( (counter == document.getLong("counter"))){
//                                    ihandleTransaction.displayError("Wrong code");
//                                } else {
//                                    addReferal(uid, document.getId(),ihandleTransaction);
//                                    break;
//                                }
                                if (!document.getId().equals(uid)) {
                                    addReferal(uid, document.getId(), ihandleTransaction);
                                    break;
                                } else {
                                    ihandleTransaction.displayError("Wrong code");
                                    break;
                                }
                            } else {
                                ihandleTransaction.displayError("Wrong code");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void addReferal(String uid, String refFrom, final IhandleTransaction ihandleTransaction){

//        if(!NetworkConnection.getInstance().networkAvailable()){
//            ihandleTransaction.displayError("No Internet access");
//            return;
//        }

        DocumentReference refRef = db.collection("users").document(uid);
        refRef
                .update("refFrom", refFrom)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        ihandleTransaction.loadActivity(0);
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

        void loadActivity(int i);

        void getSpinsLeft(long spins,boolean redirectToReferal);

        void showProgressBar(boolean b);

        void displayError(String error);

    }

    public interface IhandlCounter{

        void getCounter(long counter);

        void displayError(String e);
    }

    public interface IsideNavBar{

        void setUserImage(String img);

        void setUserName(String name);

        void navBarDataError(String error);

    }

    public interface IsetSpinCounter{


        void setCounterSuccess(boolean b);

    }

}
