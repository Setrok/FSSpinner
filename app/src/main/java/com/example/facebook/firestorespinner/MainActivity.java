package com.example.facebook.firestorespinner;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.facebook.firestorespinner.FireStore.Users;
import com.example.facebook.firestorespinner.WalletPager.WalletFragment;
import com.example.facebook.firestorespinner.ads.AdmobApplication;
import com.example.facebook.firestorespinner.screens.invite.InviteFragment;
import com.example.facebook.firestorespinner.screens.my_team.MyTeamFragment;
import com.example.facebook.firestorespinner.screens.home.HomeFragment;
import com.example.facebook.firestorespinner.screens.playspin.PlaySpinFragment;
import com.example.facebook.firestorespinner.screens.redeem.RedeemFragment;
import com.example.facebook.firestorespinner.utils.NetworkConnection;
import com.example.facebook.firestorespinner.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Users.IsideNavBar,Users.IsetSpinCounter{

    private static FragmentManager fragmentManager;

    private static final String TAG = "InfoApp";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;

    private LinearLayout layoutWarningSavePoints;
    private Button btnCancelWarning;
    private Button btnLogOutWarning;
    private Button btnAddPointsWarning;

    CircleImageView menuUserPic;
    TextView userName;

    FirebaseAuth mAuth;
    NavigationView navigationView;

    //SharedPreferences instead of DB
    public static SharedPreferences sPref;
    public static SharedPreferences.Editor prefEditor;

    //Animation
    Animation boardScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        //SharedPreferences instead of DB
        sPref = this.getSharedPreferences("com.example.facebook.firestorespinner", Context.MODE_PRIVATE);
        prefEditor = sPref.edit();

        mAuth = FirebaseAuth.getInstance();
        if(!checkIfUserLoggedIn()) {
            return;
        }

        //Interstitial
        AdmobApplication.createWallAd(this);
        AdmobApplication.requestNewInterstitial();

        //banner view
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

        //Animation
        boardScale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);

        layoutWarningSavePoints = findViewById(R.id.layout_warning_close_game);
        btnCancelWarning = findViewById(R.id.button_cancel_warning_popup);
        btnLogOutWarning = findViewById(R.id.button_log_out_popup);
        btnAddPointsWarning = findViewById(R.id.button_add_points_to_wallet);

        layoutWarningSavePoints.setVisibility(View.GONE);

        btnCancelWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutWarningSavePoints.setVisibility(View.GONE);
            }
        });

        btnLogOutWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutWarningSavePoints.setVisibility(View.GONE);
                setSpins();
            }
        });

        btnAddPointsWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutWarningSavePoints.setVisibility(View.GONE);

                Fragment PlaySpinFragment = fragmentManager
                        .findFragmentByTag(Utils.UPlaySpinFragment);

                if (PlaySpinFragment != null){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new PlaySpinFragment(),
                                    Utils.UPlaySpinFragment).commit();

                }
            }
        });

        Users.getUserData(mAuth.getCurrentUser().getUid(),this);


        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                .replace(R.id.frameContainer, new HomeFragment(),
                        Utils.UHomeFragment).commit();



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

    private void setSpins(){
        //TODO GET SPINS VALUE FROM PREFS AND SET TO LOCAL VALUE
        int spins = MainActivity.sPref.getInt("userSpins", 0);//MainActivity.sPref.getInt("userSpins", 0);
        Users.setUserSpinCounter(mAuth.getCurrentUser().getUid(),spins,this);
    }

    private void setToStart() {

        if (AccessToken.getCurrentAccessToken() == null) {
            Log.i("InfoApp","");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();


            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.signOut();
                    Log.i("InfoApp","Google signOut completed");

                    Intent startIntent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(startIntent);
                    finish();
                }
            });
        } else {

            Log.i("InfoApp","Facebook signOut");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();
                    mAuth.signOut();

                    Intent startIntent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(startIntent);
                    finish();

                }
            }).executeAsync();
        }






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

        if(id != R.id.nav_home) {
            if (!NetworkConnection.networkAvailable(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_LONG).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_home){

            Fragment HomeFragment = fragmentManager
                    .findFragmentByTag(Utils.UHomeFragment);

            if (HomeFragment != null){
                drawerLayout.closeDrawer(GravityCompat.START);
            }else {
                backToFragment(new HomeFragment(), Utils.UHomeFragment);
            }

        }
        else if (id == R.id.nav_redeem){

            Fragment RedeemFragment = fragmentManager
                    .findFragmentByTag(Utils.URedeemFragment);

            if (RedeemFragment != null){
                drawerLayout.closeDrawer(GravityCompat.START);
            }else {
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new RedeemFragment(),
                                Utils.URedeemFragment).commit();
            }

        }else if (id == R.id.nav_wallet){

            Fragment WalletFragment = fragmentManager
                    .findFragmentByTag(Utils.UWalletFragment);

            if (WalletFragment != null){
                drawerLayout.closeDrawer(GravityCompat.START);
            }else {
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new WalletFragment(),
                                Utils.UWalletFragment).commit();

            }

        }
        else if (id == R.id.nav_logout) {
            if (MainActivity.sPref.getInt("userTotalPoints", 0) != 0) {
                layoutWarningSavePoints.startAnimation(boardScale);
                layoutWarningSavePoints.setVisibility(View.VISIBLE);
            }else {
                setSpins();
            }
        }
        else if(id == R.id.nav_invite){

            Fragment inviteFragment = fragmentManager
                    .findFragmentByTag(Utils.UInviteFragment);

            if (inviteFragment != null){
                drawerLayout.closeDrawer(GravityCompat.START);
            }else {
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new InviteFragment(),
                                Utils.UInviteFragment).commit();
            }

        }
        else if(id == R.id.nav_support) {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "test@gmail.com"});
            final PackageManager pm = this.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            String className = null;
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.packageName.equals("com.google.android.gm")) {
                    className = info.activityInfo.name;

                    if(className != null && !className.isEmpty()){
                        break;
                    }
                }
            }

            emailIntent.setClassName("com.google.android.gm", className);

            try {
                startActivity(emailIntent);
            } catch(ActivityNotFoundException ex) {
                // handle error
            }

        }else if(id == R.id.nav_rate_us) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.tilseier.switchitpennywise")));
            }catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.tilseier.switchitpennywise")));
            }

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

    // Replace Login Fragment with animation
    protected void backToFragment(Fragment fragment, String tag) {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, fragment,
                        tag).commit();
    }

    @Override
    public void onBackPressed() {


        // Find the tag of signup and forgot password fragment
//        Fragment PlaySpinFragment = fragmentManager
//                .findFragmentByTag(Utils.UPlaySpinFragment);
//        Fragment WalletFragment = fragmentManager
//                .findFragmentByTag(Utils.UWalletFragment);
//        Fragment RedeemFragment = fragmentManager
//                .findFragmentByTag(Utils.URedeemFragment);
        Fragment HomeFragment = fragmentManager
                .findFragmentByTag(Utils.UHomeFragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (HomeFragment != null) {
            finish();
        }else {
            backToFragment(new HomeFragment(), Utils.UHomeFragment);
        }

    }

    @Override
    public void setCounterSuccess(boolean b) {
        if(b){
            Log.i(TAG,"Set counter success!!!!!!!!!");
            setToStart();
        } else {
            Log.i(TAG,"Set counter failure!!!!!!!!!");
        }
    }
}
