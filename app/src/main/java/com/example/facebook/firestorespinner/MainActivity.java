package com.example.facebook.firestorespinner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.facebook.firestorespinner.FireStore.Users;
import com.example.facebook.firestorespinner.ads.AdmobApplication;
import com.example.facebook.firestorespinner.screens.playspin.PlaySpinActivity;
import com.example.facebook.firestorespinner.screens.playspin.PlaySpinFragment;
import com.example.facebook.firestorespinner.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Users.IsideNavBar{

    private static FragmentManager fragmentManager;

    private static final String TAG = "InfoApp";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;

    CircleImageView menuUserPic;
    TextView userName;

    FirebaseAuth mAuth;
    NavigationView navigationView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //SharedPreferences instead of DB
    public static SharedPreferences sPref;
    public static SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();


        mAuth = FirebaseAuth.getInstance();
        if(!checkIfUserLoggedIn()) {
            return;
        }

        //Interstitial
        AdmobApplication.createWallAd(this);
        AdmobApplication.requestNewInterstitial();

        //SharedPreferences instead of DB
        sPref = this.getSharedPreferences("com.example.facebook.firestorespinner", Context.MODE_PRIVATE);
        prefEditor = sPref.edit();


        toolbar = findViewById(R.id.nav_action);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

//        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView = findViewById(R.id.main_navigation_menu);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        View headerView = navigationView.inflateHeaderView(R.layout.navigation_header);
        menuUserPic = headerView.findViewById(R.id.menu_user_pic);
        userName = headerView.findViewById(R.id.menu_user_name);

        Users.getUserData(mAuth.getCurrentUser().getUid(),this);




    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfUserLoggedIn();
    }

    private boolean checkIfUserLoggedIn(){

        Log.i(TAG,"check performed");
        if(null==mAuth.getCurrentUser()){
            setToStart();
            return false;
        }else return true;
    }

    private void setToStart() {

        mAuth.signOut();
        LoginManager.getInstance().logOut();

        Intent startIntent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(startIntent);
        finish();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_home){
//            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_redeem){
            Intent redeemIntent = new Intent(getApplicationContext(),RedeemActivity.class);
            startActivity(redeemIntent);
        }
        else if (id == R.id.nav_logout) {
            setToStart();
        }else if (id == R.id.nav_play_spin) {

            // Replace signup frgament with animation
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                    .replace(R.id.frameContainer, new PlaySpinFragment(),
                            Utils.PlaySpinFragment).commit();


//            Intent intent = new Intent(getApplicationContext(), PlaySpinActivity.class);
//            startActivity(intent);
        }

        return true;
    }

    @Override
    public void setUserImage(String img) {

        Glide.with(getApplicationContext())
                .load(img)
                .into(menuUserPic);
    }

    @Override
    public void setUserName(String name) {
        Log.i(TAG,"setUserName MA: " + name);
        userName.setText(name);

    }

    @Override
    public void navBarDataError(String error) {
        Log.i(TAG,"error");
        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
    }

}
