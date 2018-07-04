package com.example.facebook.firestorespinner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.User;
import com.example.facebook.firestorespinner.FireStore.Users;
import com.example.facebook.firestorespinner.screens.playspin.PlaySpinFragment;
import com.example.facebook.firestorespinner.utils.NetworkConnection;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,Users.IhandleTransaction{


    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "InfoApp";

    private FirebaseAuth mAuth;
    // [END declare_auth]

    private ConstraintLayout signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar progressBar;
    private ConstraintLayout fbLoginBtn;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.layout_google_login);
        signInButton.setOnClickListener(this);

        progressBar = findViewById(R.id.login_progressBar);

        //----------------------FACEBOOK LOGIN SETUP---------------------------------

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        showProgressBar(false);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showProgressBar(false);
                        Toast.makeText(LoginActivity.this, "Error signing in", Toast.LENGTH_LONG).show();
                    }
                });

        fbLoginBtn = findViewById(R.id.layout_fb_login);

        fbLoginBtn.setOnClickListener(this);

//        loginButton = findViewById(R.id.fb_login_button);
//        loginButton.setReadPermissions("email", "public_profile");
//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//                // ...
//            }
//        });

        showProgressBar(false);

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser fbUser = mAuth.getCurrentUser();
                            addUsertoFS(fbUser);
                            Log.w("InfoApp", "Ok", task.getException());
                        } else {
                            showProgressBar(false);
                            // If sign in fails, display a message to the user.
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "Account already exist with different creential",Toast.LENGTH_LONG).show();
                            } else Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signIn() {
        Log.w("InfoApp", "Clicked");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                showProgressBar(false);
                // Google Sign In failed, update UI appropriately
                Log.w("InfoApp", "Google sign in failed", e);
            }
        }
        else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("InfoApp", "signInWithCredential:success");
                            FirebaseUser googleUser = mAuth.getCurrentUser();

                            addUsertoFS(googleUser);

                            Log.w("InfoApp", "Ok", task.getException());

                        } else {
                            showProgressBar(false);
                            // If sign in fails, display a message to the user.
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "Account already exist with different creential",Toast.LENGTH_LONG).show();
                            }
                            Log.w("InfoApp", "signInWithCredential:failure", task.getException());
                            LoginManager.getInstance().logOut();
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    private void addUsertoFS(FirebaseUser googleUser) {

        User user = new User(googleUser.getDisplayName(),"",false,googleUser.getPhotoUrl().toString(),0,0,0);
        Users.addUser(googleUser.getUid(),user,LoginActivity.this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(!NetworkConnection.networkAvailable(getApplicationContext())){
            return;
        }
        if (i == R.id.layout_google_login) {

            showProgressBar(true);
            signIn();

        }
        else if(i == R.id.layout_fb_login){
            showProgressBar(true);
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        }
    }

    @Override
    public void loadActivity(int i) {

        if(i==0) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        } else if(i==1){
            Intent mainIntent = new Intent(getApplicationContext(), ReferalActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }

    }

    @Override
    public void getSpinsLeft(long spins,boolean redirectToReferal) {
        //TODO set spins value in prefs, which was received from db

        if (spins > PlaySpinFragment.limitSpins)
            spins = PlaySpinFragment.limitSpins;

        MainActivity.prefEditor.putInt("userSpins", (int)spins);
        MainActivity.prefEditor.putInt("userTotalPoints", 0);
        MainActivity.prefEditor.putInt("userRounds", 1);
        MainActivity.prefEditor.putLong("ExactTime", 0);
        MainActivity.prefEditor.putBoolean("isRated",false);

        if (spins == PlaySpinFragment.limitSpins) {
            MainActivity.prefEditor.putBoolean("isSpinnerBlocked", true);
            MainActivity.prefEditor.putBoolean("TimerWasFinished", false);
        }else {
            MainActivity.prefEditor.putBoolean("isSpinnerBlocked", false);
            MainActivity.prefEditor.putBoolean("TimerWasFinished", true);
        }

        MainActivity.prefEditor.apply();

//        Log.i("GLOBALPREF", "WORK getSpinsLeft="+spins);

        if(redirectToReferal)
            loadActivity(1);
        else
            loadActivity(0);
    }

    @Override
    public void displayError(String error) {

        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();

    }

    public void printKeyHash(){

        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.facebook.firestorespinner", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void showProgressBar(boolean show){

        if(show){
            progressBar.setVisibility(View.VISIBLE);
            signInButton.setEnabled(false);
            fbLoginBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            signInButton.setEnabled(true);
            fbLoginBtn.setEnabled(true);
        }
    }

}
