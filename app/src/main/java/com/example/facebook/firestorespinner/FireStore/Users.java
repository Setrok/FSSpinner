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
import com.google.firebase.firestore.Transaction;

import java.io.File;

public class Users {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    static final String TAG ="InfoAppFS";

    public static void addUser(String uID, final User user, final IhandleTransaction ihandleTransaction){

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final DocumentReference userDocRef = db.collection("users").document(uID);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDocRef);

                if(!snapshot.exists()){
                    transaction.set(userDocRef,user);
                } else{
                    transaction.update(userDocRef,"picture",user.getPicture());
                }
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
                ihandleTransaction.loadActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                    ihandleTransaction.displayError(e);
                }
            });

    }

    public static void getUserData(String uid, final IsideNavBar isideNavBar){

        final DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                isideNavBar.setUserName(user.getName());
                isideNavBar.setUserImage(user.getPicture());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isideNavBar.navBarDataError(e.getLocalizedMessage());
            }
        });

    }

    public interface IhandleTransaction{

        void loadActivity();

        void displayError(Exception e);

    }

    public interface IsideNavBar{

        void setUserImage(String img);

        void setUserName(String name);

        void navBarDataError(String error);

    }

}
