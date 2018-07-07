package com.example.facebook.firestorespinner.screens.playspin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.example.facebook.firestorespinner.FireStore.Users;
import com.example.facebook.firestorespinner.MainActivity;
import com.example.facebook.firestorespinner.R;
import com.example.facebook.firestorespinner.ads.AdmobApplication;
import com.example.facebook.firestorespinner.screens.home.HomeFragment;
import com.example.facebook.firestorespinner.utils.NetworkConnection;
import com.example.facebook.firestorespinner.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class PlaySpinFragment extends Fragment implements RewardedVideoAdListener,ScoreManager.IscoreMessage, Users.IsetSpinCounter {

    private View view;
    private Context context;

    String uid;// = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private static FragmentManager fragmentManager;

    ImageView imageIcon;
    ImageView imageWheel;
    ImageView imageSpin;

    LinearLayout layoutBoard;
    LinearLayout layoutLimitReach;
    LinearLayout layoutTimeLimit;

    Button buttonWonOK;
    Button buttonLimitReachOK;
    Button buttonSpinNow;
    Button buttonAddToWallet;
    Button buttonWatchVideo;

    //TIMER
    TextView hoursTen;
    TextView hour;
    TextView semiColum1;
    TextView minTen;
    TextView min;
    TextView secTen;
    TextView sec;
    //text
    TextView textWonPoints;
    TextView textTitleWonPoints;
    TextView textTotalPoints;
    TextView textCurrentRound;
    TextView textLimitReached;

    //Video Reward Ads
    private RewardedVideoAd mRewardedVideoAd;
    private static final String rewardAdID = "ca-app-pub-3940256099942544/5224354917";
    private boolean isReward = false;

    private Integer spinPoints = 0;

    long countdown;
    static CountDownTimer cdt;

    Animation boardScale;

    @Override
    public void setCounterSuccess(boolean b) {

    }

    enum GameState {SPIN, ENABLE, WON_BOARD, BLOCKED, LIMIT_BOARD_MASSAGE};//RATING_BAR
    GameState currentState;

    Random rand;
    FirebaseAuth mAuth;

    int degree = 0, degree_prev = 0;

    int rounds = 1, spins = 0;
    public static int limitSpins = 3;
    int totalPoints = 0;

    //DAILY REWARD
//    ImageView[] imageDayRewards;
//    private int[] images;

    // (360 / 10 sectors) / 2
    private static final float FACTOR = 18;

    public static boolean fromProfile = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_play_spin, container, false);
        initViews();
        setListeners();
        return view;

    }

    private void setListeners() {

    }

    private void initViews() {

        context = getActivity();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        mAuth = FirebaseAuth.getInstance();

        currentState = GameState.ENABLE;



        //Video Reward Ads
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(this);



        textWonPoints = (TextView) view.findViewById(R.id.text_won_points);
        textTitleWonPoints = (TextView) view.findViewById(R.id.text_won);
        textTotalPoints = (TextView) view.findViewById(R.id.text_total_points);
        textCurrentRound = (TextView) view.findViewById(R.id.text_current_round);
        textLimitReached = (TextView) view.findViewById(R.id.text_limit_rich);

        imageIcon = (ImageView) view.findViewById(R.id.image_mini_icon);
        imageWheel = (ImageView) view.findViewById(R.id.image_wheel);
        imageSpin = (ImageView) view.findViewById(R.id.image_spin);

        buttonWonOK = (Button) view.findViewById(R.id.button_ok);
        buttonLimitReachOK = (Button) view.findViewById(R.id.button_ok_limit_reach);
        buttonSpinNow = (Button) view.findViewById(R.id.button_spin_now);
        buttonAddToWallet = (Button) view.findViewById(R.id.button_add_to_wallet);
        buttonWatchVideo = (Button) view.findViewById(R.id.button_watch_video);

        layoutBoard = (LinearLayout) view.findViewById(R.id.layout_board);
        layoutLimitReach = (LinearLayout) view.findViewById(R.id.layout_limit_reach);
        layoutTimeLimit = (LinearLayout) view.findViewById(R.id.layout_time_limit);

        //TIMER
        hoursTen = view.findViewById(R.id.textHoursTen);
        hour = view.findViewById(R.id.textHours);
        semiColum1 = view.findViewById(R.id.text_semi_colum_1);
        minTen = view.findViewById(R.id.textMinTen);
        min = view.findViewById(R.id.textMin);
        secTen = view.findViewById(R.id.textSecTen);
        sec = view.findViewById(R.id.textSec);

        boardScale = AnimationUtils.loadAnimation(context, R.anim.anim_scale);

        rand = new Random();

//        MainActivity.prefEditor.putInt("userSpins", 0).apply();
//        MainActivity.prefEditor.putInt("userRounds", 1).apply();

        totalPoints = MainActivity.sPref.getInt("userTotalPoints", 0);
        spins = MainActivity.sPref.getInt("userSpins", 0);
        rounds = MainActivity.sPref.getInt("userRounds", 1);
        textCurrentRound.setText(getString(R.string.text_current_round, rounds));


        if(spins >= limitSpins){
            currentState = GameState.BLOCKED;
            layoutLimitReach.startAnimation(boardScale);
            layoutLimitReach.setVisibility(View.VISIBLE);
            MainActivity.prefEditor.putBoolean("isSpinnerBlocked", false);
            MainActivity.prefEditor.apply();
        }


        if (MainActivity.sPref.getBoolean("isSpinnerBlocked", false)){
            showLockTimer();
        }else {
            layoutTimeLimit.setVisibility(View.INVISIBLE);
        }

        Log.i("SPINS_COUNT", ""+spins);
        Log.i("ROUNDS_COUNT", ""+rounds);

        textTotalPoints.setText(""+totalPoints);

        setActiveSpins(limitSpins, false);
        setActiveSpins(spins, true);

        imageSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                }

//                if (MainActivity.isInternetConnection) {

                if (currentState != GameState.BLOCKED) {
                    layoutTimeLimit.setVisibility(View.INVISIBLE);
                    spinWheel();
                }

//                }else {
//
//                    Toast.makeText(getApplicationContext(),"This app needs internet connection!", Toast.LENGTH_SHORT).show();
//
//                }

            }
        });

        buttonWonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                }

//                Integer tPoints = MainActivity.sPref.getInt("userTotalPoints", 0);

                if (spinPoints >= 1){
                    addToWallet(spinPoints);
                }

                if (currentState == GameState.LIMIT_BOARD_MASSAGE){

                    textLimitReached.setText(getString(R.string.text_limit_reach_game, 1, "hour"));

//                    if(rounds > 2)
//                        textLimitReached.setText(getString(R.string.text_limit_reach_game, (rounds - 1), "hours"));
//                    else
//                        textLimitReached.setText(getString(R.string.text_limit_reach_game, 1, "hour"));

                    layoutLimitReach.startAnimation(boardScale);
                    layoutLimitReach.setVisibility(View.VISIBLE);
                }
//                else if (currentState == GameState.RATING_BAR){
////                    layoutRatingBoard.startAnimation(boardScale);
////                    layoutRatingBoard.setVisibility(View.VISIBLE);
//                }
                else {
//                    currentState = GameState.ENABLE;
                    currentState = GameState.BLOCKED;
                    showLockTimer();
                }

                layoutBoard.setVisibility(View.GONE);


            }
        });

        buttonLimitReachOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentState = GameState.BLOCKED;
                layoutLimitReach.setVisibility(View.GONE);

                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new HomeFragment(),
                                Utils.UHomeFragment).commit();

//                showLockTimer();

            }
        });

        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(context, MainActivity.class);
//                startActivity(intent);
//
//                showInterstitial();

            }
        });

        buttonAddToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                }

                Integer tPoints = MainActivity.sPref.getInt("userTotalPoints", 0);

                if (tPoints >= 1){

                    addToWallet(tPoints);

                }else {
                    Toast.makeText(context, "Please, earn more coins", Toast.LENGTH_SHORT).show();
                }


            }
        });

        buttonWatchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRewardedVideoAd.show();
                buttonWatchVideo.setVisibility(View.GONE);

            }
        });

        buttonSpinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                }

                //                if (MainActivity.isInternetConnection) {

                if (currentState != GameState.BLOCKED) {
                    layoutTimeLimit.setVisibility(View.INVISIBLE);
                    spinWheel();
                }

//                }else {
//
//                    Toast.makeText(getApplicationContext(),"This app needs internet connection!", Toast.LENGTH_SHORT).show();
//
//                }

            }
        });

        //LoadVideo
        loadRewardedVideoAd();


    }

    private void onNoInternetConnection(){

        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new HomeFragment(),
                        Utils.UHomeFragment).commit();

    }

    private void showInterstitial(){

        if (AdmobApplication.isAdLoaded()){

            AdmobApplication.displayLoadedAd();

            AdmobApplication.mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    AdmobApplication.requestNewInterstitial();
                }
            });

        }else{
            AdmobApplication.requestNewInterstitial();
        }

    }

    private void showLockTimer(){

        if(spins >= limitSpins) {

//            layoutLimitReach.setVisibility(View.VISIBLE);

//            fragmentManager
//                    .beginTransaction()
//                    .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
//                    .replace(R.id.frameContainer, new HomeFragment(),
//                            Utils.UHomeFragment).commit();

//            showInterstitial();
//            layoutLimitReach.setVisibility(View.VISIBLE);
        }else {
            setTimer();
            loadRewardedVideoAd();
            startCountDown();
        }

    }

    private void spinWheel() {

        if (currentState == GameState.ENABLE) {

            currentState = GameState.SPIN;

            degree_prev = degree % 360; // Always To Right
            degree = getDegree();

//            Log.i("degree", ""+degree);
//            Log.i("abs(degree-degree_prev)", ""+Math.abs(degree-degree_prev));

//            Log.i("degree-degree_prev", ""+(degree-degree_prev));

            while (Math.abs(degree-degree_prev) < 360){
                degree = getDegree();
            }

//            Log.i("Degree degree-prev",""+(degree-degree_prev));//-1943
//
//            if (degree <= degree_prev){
//                Log.i("Degree To ","Left");
//            }else {
//                Log.i("Degree To ","Right");
//            }
//
//            Log.i("Degree", ""+degree);
//            Log.i("Degree Prev", ""+degree_prev);


            RotateAnimation rotate = new RotateAnimation(degree_prev, degree,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            rotate.setDuration(5000);//3600
            rotate.setFillAfter(true);
            rotate.setInterpolator(new DecelerateInterpolator());
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    textTitleWonPoints.setText(R.string.won_points);
                    textWonPoints.setText("0");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentState = GameState.ENABLE;

                    Log.i("Degree Current", ""+(360 - (degree % 360)));

                    int wonPoints = getCurrentPoints(360 - (degree % 360));

                    addTotalPoints(wonPoints);

                    currentState = GameState.WON_BOARD;

                    if (spinPoints >= 1) {
                        buttonWonOK.setText("Add To Wallet");
                    }else{
                        buttonWonOK.setText("Ok");
                    }

                    layoutBoard.startAnimation(boardScale);
                    layoutBoard.setVisibility(View.VISIBLE);

                    spins++;
                    MainActivity.prefEditor.putInt("userSpins", spins).apply();
                    updateSpins(spins);


                    setActiveSpins(spins, true);

//                    if (spins % 2 != 0 && spins != 5){
//
//                        showInterstitial();
//
//                    }

                    if (spins >= limitSpins){

                        currentState = GameState.LIMIT_BOARD_MASSAGE;
                        spins = limitSpins;

                        //TIMER
//                        setTimer();

//                        MainActivity.prefEditor.putBoolean("isSpinnerBlocked", true).apply();

                    }
//                    else if ((rounds == 2 && spins == 3) || (rounds == 3 && spins == 4 && !MainActivity.sPref.getBoolean("isRated",false))){
//                        currentState = GameState.RATING_BAR;
//                    }

                    //TIMER
                    setTimer();

                    MainActivity.prefEditor.putBoolean("isSpinnerBlocked", true).apply();



                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            imageWheel.startAnimation(rotate);

        }

    }

    private void updateSpins(int spins){
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int quizTries = MainActivity.sPref.getInt("DayQuizLimit", 0);
        Users.setUserSpinCounter(uid,spins, quizTries, this);
    }

    private void setActiveSpins(int spin_counter, boolean active){

        if (spin_counter > limitSpins)
            spin_counter = limitSpins;

        if (active) {

            for (int i = 0; i < spin_counter; i++) {
                int resID = getResources().getIdentifier("spin_" + (i + 1), "id", Objects.requireNonNull(context).getPackageName());
                TextView activeSpin = (TextView) view.findViewById(resID);
                activeSpin.setBackgroundResource(R.drawable.counter_passive_style);
            }

        }else {

            for (int i = 0; i < spin_counter; i++) {
                int resID = getResources().getIdentifier("spin_" + (i + 1), "id", Objects.requireNonNull(context).getPackageName());
                TextView activeSpin = (TextView) view.findViewById(resID);
                activeSpin.setBackgroundResource(R.drawable.counter_active_style);
            }

        }
    }

    private void addTotalPoints(int points){

        spinPoints = points;

        totalPoints += points;
        MainActivity.prefEditor.putInt("userTotalPoints", totalPoints).apply();
        textWonPoints.setText("" + points);
        textTotalPoints.setText("" + totalPoints);
        if (points != 0) {
            textTitleWonPoints.setText(R.string.won_points);
        } else {
            textTitleWonPoints.setText(R.string.bad_luck);
        }

    }

    private int getDegree(){

        int randDegree = 0;
        int fitDegree = 0;

        //1 round 1 spin 100 - 500 2-5 spins 10 - 100
        //2 round 1 spin 60 - 300 2-5 spins 10 - 500
        //3 round 1 spin 300 - 1000 2-5 spins 10 - 200
        //4 round 1 spin 100 - 500 2-5 spins 10 - 100
        //5 round 1 spin 60 - 300 2-5 spins 10 - 500
        //6 round 1 spin 300 -1000 2-5 spins 10 - 200
        //...


        //100-500: ((int)FACTOR * 4) + (int)FACTOR + 2   ---   ((int)FACTOR * 12) + (int)FACTOR - 2
        //60-300:  ((int)FACTOR * 2) + (int)FACTOR + 2   ---   ((int)FACTOR * 10) + (int)FACTOR - 2
        //300-1000: ((int)FACTOR * 8) + (int)FACTOR + 2  ---   ((int)FACTOR * 16) + (int)FACTOR - 2
        //10-100:   ((int)FACTOR * 16) + (int)FACTOR + 2  ---  ((int)FACTOR * 26) + (int)FACTOR - 2
        //10-500:   ((int)FACTOR * 16) + (int)FACTOR + 2  ---  ((int)FACTOR * 32) + (int)FACTOR - 2
        //10-200:   ((int)FACTOR * 16) + (int)FACTOR + 2  ---  ((int)FACTOR * 28) + (int)FACTOR - 2

        int Low = 0;
        int High = 180;


        Low = ((int)FACTOR * 16) + (int)FACTOR + 2;
        High = ((int)FACTOR * 28) + (int)FACTOR - 2;


//        if (rounds % 3 == 1){
//
//            if ((spins+1) == 1){
////                Log.i("1 round", "1 spin");
//
//                Low = ((int)FACTOR * 4) + (int)FACTOR + 2;
//                High = ((int)FACTOR * 12) + (int)FACTOR - 2;
//
//            }else {
////                Log.i("1 round", "2-5 spins");
//
//                Low = ((int)FACTOR * 16) + (int)FACTOR + 2;
//                High = ((int)FACTOR * 26) + (int)FACTOR - 2;
//
//            }
//
//        }else if (rounds % 3 == 2){
//
//            if ((spins+1) == 1){
////                Log.i("2 round", "1 spin");
//
//                Low = ((int)FACTOR * 2) + (int)FACTOR + 2;
//                High = ((int)FACTOR * 10) + (int)FACTOR - 2;
//
//            }else {
////                Log.i("2 round", "2-5 spins");
//
//                Low = ((int)FACTOR * 16) + (int)FACTOR + 2;
//                High = ((int)FACTOR * 32) + (int)FACTOR - 2;
//            }
//
//        }else if (rounds % 3 == 0){
//
//            if ((spins+1) == 1){
////                Log.i("3 round", "1 spin");
//
//                Low = ((int)FACTOR * 8) + (int)FACTOR + 2;
//                High = ((int)FACTOR * 16) + (int)FACTOR - 2;
//            }else {
////                Log.i("3 round", "2-5 spins");
//
//                Low = ((int)FACTOR * 16) + (int)FACTOR + 2;
//                High = ((int)FACTOR * 28) + (int)FACTOR - 2;
//            }
//
//        }

        fitDegree = (rand.nextInt(High-Low) + Low) % 360;//(degree % 360)

        Log.i("Degree Fit", ""+fitDegree);

        randDegree = 360 - fitDegree + (360 * (rand.nextInt(10) + 1));

        return randDegree;

    }

    private int getCurrentPoints(int degrees){

        int points = 0;

        if (degrees >= (FACTOR * 1) && degrees < (FACTOR * 3)){
            points = 2;
        }
        if (degrees >= (FACTOR * 3) && degrees < (FACTOR * 5)){
            points = 3;
        }
        if (degrees >= (FACTOR * 5) && degrees < (FACTOR * 7)){
            points = 4;
        }
        if (degrees >= (FACTOR * 7) && degrees < (FACTOR * 9)){
            points = 5;
        }
        if (degrees >= (FACTOR * 9) && degrees < (FACTOR * 11)){
            points = 10;
        }
        if (degrees >= (FACTOR * 11) && degrees < (FACTOR * 13)){
            points = 20;
        }
        if (degrees >= (FACTOR * 13) && degrees < (FACTOR * 15)){
            points = 25;
        }
        if (degrees >= (FACTOR * 15) && degrees < (FACTOR * 17)){
            points = 50;
        }
        if (degrees >= (FACTOR * 17) && degrees < (FACTOR * 19)){
            points = 0;
        }

        if ((degrees >= (FACTOR * 19) && degrees <= 360) || (degrees >= 0 && degrees < (FACTOR * 1))){
            points = 1;
        }

        return points;

    }


    private void setTimer() {

        if(MainActivity.sPref.getLong("ExactTime",0) == 0) {

            MainActivity.prefEditor.putLong("TomorrowTime", getTomorrowTime());
            MainActivity.prefEditor.putLong("ExactTime", System.currentTimeMillis());
            MainActivity.prefEditor.putBoolean("TimerWasFinished",false);
            MainActivity.prefEditor.apply();

        }

    }

    private long getTomorrowTime() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int sec = Calendar.getInstance().get(Calendar.SECOND);

//        Log.i("Info","hour is" + hour + "min is" + min + "sec is" + sec);

        return (System.currentTimeMillis()- hour*3600*1000 - min*1000*60 - sec*1000) + (24*3600*1000);

    }

    public void startCountDown(){

        countdown = MainActivity.sPref.getLong("ExactTime",System.currentTimeMillis());
        long tomorrowTime = MainActivity.sPref.getLong("TomorrowTime",getTomorrowTime());

//        Log.i("Info",countdown+"");

//        if(null!=cdt) cdt.cancel();
        closeTimer();
        int multiplier = 1;

        rounds = MainActivity.sPref.getInt("userRounds", 1);
        spins = MainActivity.sPref.getInt("userSpins", 0);

        if(rounds > 2){
            multiplier = rounds - 1;
        }

        long millis;
        if (spins == limitSpins)
            millis = tomorrowTime;//getTomorrowTime();//countdown + 1000*3600;// * multiplier;
        else
            millis = countdown + 60000;// * multiplier;


        if(! (System.currentTimeMillis() >= millis)) {//1000*3600
            currentState = GameState.BLOCKED;
            layoutTimeLimit.startAnimation(boardScale);
            layoutTimeLimit.setVisibility(View.VISIBLE);
            cdt = new CountDownTimer(millis - System.currentTimeMillis(), 1000) {//1000*3600

                public void onTick(long millisUntilFinished) {

                    // Method
                    setTimerText(millisUntilFinished);

                }

                public void onFinish() {

                    try {
//                        Toast.makeText(context, "Unlock Spinner", Toast.LENGTH_SHORT).show();
                        isReward = false;
                        unlockSpinner();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }.start();
        } else {

            if(!MainActivity.sPref.getBoolean("TimerWasFinished",false)){
                try {
//                    Toast.makeText(context, "Unlock Spinner 2", Toast.LENGTH_SHORT).show();
                    isReward = false;
                    unlockSpinner();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }

    }

    public static void closeTimer(){
        if(null!=cdt) {
            cdt.cancel();
            cdt = null;
        }
    }

    private void unlockSpinner(){

        MainActivity.prefEditor.putLong("ExactTime", 0);
        MainActivity.prefEditor.putLong("TomorrowTime", 0);
        MainActivity.prefEditor.putBoolean("isSpinnerBlocked", false);
        MainActivity.prefEditor.putBoolean("TimerWasFinished",true);

        rounds = MainActivity.sPref.getInt("userRounds", 1);
        spins = MainActivity.sPref.getInt("userSpins", 0);

        if (spins == limitSpins && !isReward) {
            spins = 0;
            rounds++;
            textCurrentRound.setText(getString(R.string.text_current_round, rounds));
            MainActivity.prefEditor.putInt("userSpins", spins);
            MainActivity.prefEditor.putInt("userRounds", rounds);
            setActiveSpins(limitSpins, false);
        }else if (spins == limitSpins && isReward){

            spins = limitSpins - 1;
            textCurrentRound.setText(getString(R.string.text_current_round, rounds));
            MainActivity.prefEditor.putInt("userSpins", spins);
            setActiveSpins(limitSpins, false);
            setActiveSpins(spins, true);

        }
        isReward = false;
        MainActivity.prefEditor.apply();

        currentState = GameState.ENABLE;
        layoutTimeLimit.setVisibility(View.INVISIBLE);

//        Toast.makeText(context,"Round "+rounds,Toast.LENGTH_LONG).show();

    }

    private void setTimerText(long millisUntilFinished) {

        int hours = (int) millisUntilFinished/1000/3600;
        int mins = (int) millisUntilFinished/1000/60 - hours*60;
        int secs = (int) (millisUntilFinished - hours*1000*3600 - mins*60*1000)/1000;

        Log.i("Info","mins left: " + mins + "Secs left "+ secs);

        if(hours>0){

            hoursTen.setText(""+hours/10);
            hour.setText(""+hours%10);

            hoursTen.setVisibility(View.VISIBLE);
            hour.setVisibility(View.VISIBLE);
            semiColum1.setVisibility(View.VISIBLE);
        } else {
            hoursTen.setText("0");
            hour.setText("0");

            hoursTen.setVisibility(View.GONE);
            hour.setVisibility(View.GONE);
            semiColum1.setVisibility(View.GONE);
        }
        minTen.setText(""+mins/10);
        min.setText(""+mins%10);
        secTen.setText(""+secs/10);
        sec.setText(""+secs%10);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!fromProfile) {
            if (MainActivity.sPref.getBoolean("isSpinnerBlocked", false))
                showInterstitial();
        }else {
            fromProfile = false;
        }

    }

    public void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(rewardAdID,
                    new AdRequest.Builder()
                            .addTestDevice("EC07F4759620B8F1E3BD5F493490BEB4")
                            .build());
//.addTestDevice("EC07F4759620B8F1E3BD5F493490BEB4")
//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        }else {
            if (spins == limitSpins) {
                buttonWatchVideo.setVisibility(View.GONE);//VISIBLE
            }else{
                buttonWatchVideo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        if (MainActivity.sPref.getBoolean("isSpinnerBlocked", false)){
            isReward = true;
            unlockSpinner();
        }
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

    private void addToWallet(int tPoints){

        Toast.makeText(context, "Adding to wallet...", Toast.LENGTH_SHORT).show();

        ScoreManager.addScore(mAuth.getCurrentUser().getUid(),
                tPoints,"Spin Bonus",true,false,this);

    }

    @Override
    public void displayError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scoreAddSuccess() {
        totalPoints = 0;
        MainActivity.prefEditor.putInt("userTotalPoints", totalPoints).apply();
        textTotalPoints.setText(""+totalPoints);
    }

}
