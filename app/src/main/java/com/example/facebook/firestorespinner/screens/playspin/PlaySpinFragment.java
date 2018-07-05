package com.example.facebook.firestorespinner.screens.playspin;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.example.facebook.firestorespinner.MainActivity;
import com.example.facebook.firestorespinner.R;
import com.example.facebook.firestorespinner.ads.AdmobApplication;
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

public class PlaySpinFragment extends Fragment implements RewardedVideoAdListener,ScoreManager.IscoreMessage {

    private View view;
    private Context context;

    ImageView imageIcon;
    ImageView imageWheel;
    ImageView imageSpin;

    LinearLayout layoutBoard;
    LinearLayout layoutLimitReach;
    LinearLayout layoutTimeLimit;
//    LinearLayout layoutDailyReward;
//    LinearLayout layoutRatingBoard;
//    LinearLayout layoutWarningClose;

    Button buttonWonOK;
//    Button buttonGetReward;
    Button buttonLimitReachOK;
    Button buttonGetFollowers;
//    Button buttonYesClose;
//    Button buttonNoClose;
//    Button buttonShare;
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

    long countdown;
    static CountDownTimer cdt;

    Animation boardScale;

    //RATING BAR
//    RatingBar ratingBar;
    //    Button buttonRate;
//    Button buttonNoRate;
//    Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_on);
//    int heightStar = mBitmap.getHeight();
//    boolean redirected = false;
//    long date_firstLaunch = 0;
//    long timeOff = 0;
//    float ratingMark = 1.0f;

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
//        buttonGetReward = (Button) view.findViewById(R.id.button_get_reward);
        buttonLimitReachOK = (Button) view.findViewById(R.id.button_ok_limit_reach);
//        buttonRate = (Button) view.findViewById(R.id.button_rate);
//        buttonNoRate = (Button) view.findViewById(R.id.button_no_rate);
        buttonGetFollowers = (Button) view.findViewById(R.id.button_get_followers);
//        buttonYesClose = (Button) view.findViewById(R.id.button_yes_close_game);
//        buttonNoClose = (Button) view.findViewById(R.id.button_no_close_game);
//        buttonShare = (Button) view.findViewById(R.id.button_share_close_game);
        buttonAddToWallet = (Button) view.findViewById(R.id.button_add_to_wallet);
        buttonWatchVideo = (Button) view.findViewById(R.id.button_watch_video);

        layoutBoard = (LinearLayout) view.findViewById(R.id.layout_board);
        layoutLimitReach = (LinearLayout) view.findViewById(R.id.layout_limit_reach);
        layoutTimeLimit = (LinearLayout) view.findViewById(R.id.layout_time_limit);
//        layoutDailyReward = (LinearLayout) view.findViewById(R.id.layout_daily_rewards_board);
//        layoutWarningClose = (LinearLayout) view.findViewById(R.id.layout_warning_close_game);

        //TIMER
        hoursTen = view.findViewById(R.id.textHoursTen);
        hour = view.findViewById(R.id.textHours);
        semiColum1 = view.findViewById(R.id.text_semi_colum_1);
        minTen = view.findViewById(R.id.textMinTen);
        min = view.findViewById(R.id.textMin);
        secTen = view.findViewById(R.id.textSecTen);
        sec = view.findViewById(R.id.textSec);

        boardScale = AnimationUtils.loadAnimation(context, R.anim.anim_scale);

        //RATING BAR
//        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
//        layoutRatingBoard = (LinearLayout) view.findViewById(R.id.layout_stars_board);

        //DAILY REWARDS
//        imageDayRewards = new ImageView[6];
//        imageDayRewards[0] = view.findViewById(R.id.image_day_1);
//        imageDayRewards[1] = view.findViewById(R.id.image_day_2);
//        imageDayRewards[2] = view.findViewById(R.id.image_day_3);
//        imageDayRewards[3] = view.findViewById(R.id.image_day_4);
//        imageDayRewards[4] = view.findViewById(R.id.image_day_5);
//        imageDayRewards[5] = view.findViewById(R.id.image_day_6);
//
//        images = new int[7];
//        images[0] = R.drawable.coin_1;
//        images[1] = R.drawable.coin_2;
//        images[2] = R.drawable.coin_3;
//        images[3] = R.drawable.coin_4;
//        images[4] = R.drawable.coin_5;
//        images[5] = R.drawable.coin_6;
//        images[6] = R.drawable.coin_locked;

//        if (MainActivity.profileImage != null) {
//            imageIcon.setImageBitmap(MainActivity.profileImage);
//        }

        rand = new Random();

//        MainActivity.prefEditor.putInt("userSpins", 0).apply();
//        MainActivity.prefEditor.putInt("userRounds", 1).apply();

        totalPoints = MainActivity.sPref.getInt("userTotalPoints", 0);
        spins = MainActivity.sPref.getInt("userSpins", 0);
        rounds = MainActivity.sPref.getInt("userRounds", 1);
        textCurrentRound.setText(getString(R.string.text_current_round, rounds));

        if (MainActivity.sPref.getBoolean("isSpinnerBlocked", false)){
            showLockTimer();
        }else {
            layoutTimeLimit.setVisibility(View.INVISIBLE);
        }

        Log.i("SPINS_COUNT", ""+spins);
        Log.i("ROUNDS_COUNT", ""+rounds);

        textTotalPoints.setText(""+totalPoints);


        //Daily Reward
//        checkForStars();


        setActiveSpins(limitSpins, false);
        setActiveSpins(spins, true);

        imageSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                showLockTimer();

            }
        });

        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);

                showInterstitial();

            }
        });

        buttonAddToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer tPoints = MainActivity.sPref.getInt("userTotalPoints", 0);

                if (tPoints >= 1){

                    addToWallet(tPoints);

                }else {
                    Toast.makeText(context, "Please, earn more points", Toast.LENGTH_SHORT).show();
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



//        buttonGetReward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                layoutDailyReward.setVisibility(View.INVISIBLE);
//
//            }
//        });

//        buttonRate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layoutRatingBoard.setVisibility(View.INVISIBLE);
//                currentState = GameState.ENABLE;
//
//                if(ratingMark<=3)
//                    Toast.makeText(getApplicationContext(),"Thank you!", Toast.LENGTH_SHORT).show();
//                else if(ratingMark>=3){
//                    date_firstLaunch = System.currentTimeMillis();
//                    redirected = true;
//
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("market://details?id=com.fingersoft.hillclimb")));//CHANGE TO APP LINK
//                    }catch (ActivityNotFoundException e){
//                        startActivity(new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("https://play.google.com/store/apps/details?id=com.fingersoft.hillclimb")));//CHANGE TO APP LINK
//                    }
//
//                }
//
//            }
//        });
//        buttonNoRate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layoutRatingBoard.setVisibility(View.INVISIBLE);
//                currentState = GameState.ENABLE;
//            }
//        });


        buttonGetFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


//        ratingBar.getLayoutParams().height = heightStar;

//        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
//
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating,
//                                        boolean fromUser) {
//                // TODO Auto-generated method stub
//                ratingMark = rating;
//
//                if(ratingMark<=3)
//                    Toast.makeText(context,"Thank you!", Toast.LENGTH_SHORT).show();
//                else if(ratingMark>=3){
//                    date_firstLaunch = System.currentTimeMillis();
//                    redirected = true;
//
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("market://details?id=com.fingersoft.hillclimb")));//CHANGE TO APP LINK
//                    }catch (ActivityNotFoundException e){
//                        startActivity(new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("https://play.google.com/store/apps/details?id=com.fingersoft.hillclimb")));//CHANGE TO APP LINK
//                    }
//
//                }
//
//                layoutRatingBoard.setVisibility(View.INVISIBLE);
//                currentState = GameState.ENABLE;
//
//            }
//
//        });



//        buttonYesClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                finish();
//
//            }
//        });
//
//        buttonNoClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layoutWarningClose.setVisibility(View.INVISIBLE);
//            }
//        });
//
//        buttonShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                layoutWarningClose.setVisibility(View.INVISIBLE);
//
//                shareApp(MainActivity.sPref.getInt("userTotalPoints", 10));
//
//            }
//        });


        //LoadVideo
        loadRewardedVideoAd();


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

//        if(spins == limitSpins) {
//            showInterstitial();
//        }
        setTimer();
        loadRewardedVideoAd();
        startCountDown();

    }

//    private void showDailyReward(){
//        layoutDailyReward.startAnimation(boardScale);
//        layoutDailyReward.setVisibility(View.VISIBLE);
//    }

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

                    layoutBoard.startAnimation(boardScale);
                    layoutBoard.setVisibility(View.VISIBLE);

                    spins++;
                    MainActivity.prefEditor.putInt("userSpins", spins).apply();

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

            MainActivity.prefEditor.putLong("ExactTime", System.currentTimeMillis());
            MainActivity.prefEditor.putBoolean("TimerWasFinished",false);
            MainActivity.prefEditor.apply();

        }

    }

    public void startCountDown(){

        countdown = MainActivity.sPref.getLong("ExactTime",System.currentTimeMillis());

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
            millis = countdown + 1000*3600;// * multiplier;
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

//            Log.i("Info","Timer was gone");
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


    //DAILY REWARDS


//    public void checkForStars(){
//
//        //prefs.edit().putInt("DayOfReward", 0).apply();//to be commented
//        //prefs.edit().putLong("DailyReward", 0).apply();
//
////        if( prefs.getInt("DayOfReward",-1) == -1){
////
////            prefs.edit().putInt("DayOfReward", 0).apply();
////        }
//
//
//        long prefExist = MainActivity.sPref.getLong("DailyReward",0);
//
//        if(prefExist == 0|| MainActivity.sPref.getInt("DayOfReward",0) == 0){
//
//            addDaytoPrefs();
//            addTotalPoints(10);
//            setDateToZero();
//            showDailyReward();
////            Toast.makeText(GameActivity.this,"First reward",Toast.LENGTH_LONG).show();
//
//        }
//        //setDateToZero();
//
//
////Just in time //3600*24 //3600*48
//        if( (System.currentTimeMillis() - MainActivity.sPref.getLong("DailyReward",0) ) / 1000 >= 3600*24
//                && (System.currentTimeMillis() - MainActivity.sPref.getLong("DailyReward",0) ) / 1000 <=  3600*48)
//        {
//            addDaytoPrefs();
//            setDateToZero();
//
//            showDailyReward();
//            addTotalPoints(MainActivity.sPref.getInt("DayOfReward",1)*10);
//
////            Toast.makeText(GameActivity.this,"Get your reward, day:"+MainActivity.sPref.getInt("DayOfReward",0),Toast.LENGTH_LONG).show();
////Too late loser
//        } else if((System.currentTimeMillis() - MainActivity.sPref.getLong("DailyReward",0) ) / 1000 >=  3600*48){
//
//            MainActivity.prefEditor.putInt("DayOfReward",1).apply();
//            setDayImages(1);
//
//            showDailyReward();
//
//            addTotalPoints(10);
//
//            setDateToZero();
////            Toast.makeText(GameActivity.this,"Missed reward, get first"+MainActivity.sPref.getInt("DayOfReward",0),Toast.LENGTH_LONG).show();
////Too early
//        } else {
//            setDayImages(MainActivity.sPref.getInt("DayOfReward",0));
////            Toast.makeText(GameActivity.this,"Too early " + MainActivity.sPref.getInt("DayOfReward",0),Toast.LENGTH_LONG).show();
//        }
//
//    }



//    private void addDaytoPrefs() {
//        int currentDayOfReward = MainActivity.sPref.getInt("DayOfReward",0);
//
//        if(currentDayOfReward<6) {
//            MainActivity.prefEditor.putInt("DayOfReward", (currentDayOfReward + 1)).apply();
//            setDayImages(currentDayOfReward + 1);
//        }
//        else {
//            MainActivity.prefEditor.putInt("DayOfReward", 1).apply();
//            setDayImages(1);
//        }
//
//    }

//    private void setDayImages(int currentDayOfReward) {
//
//        Log.i("Info","current images unlocked"+ (currentDayOfReward+1));
//        for(int i = 0;i<6;i++){
//
//            if(i<currentDayOfReward)
//                imageDayRewards[i].setImageResource(images[i]);
//            else imageDayRewards[i].setImageResource(images[6]);
//
//        }
//
//    }
//
//    private void setDateToZero() {
//
//        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        int min = Calendar.getInstance().get(Calendar.MINUTE);
//        int sec = Calendar.getInstance().get(Calendar.SECOND);
//
//        Log.i("Info","hour is" + hour + "min is" + min + "sec is" + sec);
//        MainActivity.prefEditor.putLong("DailyReward", System.currentTimeMillis()- hour*3600*1000 - min*1000*60 - sec*1000).apply();
//
////        MainActivity.prefEditor.putLong("DailyReward", System.currentTimeMillis()).apply();
//
//    }


    public void shareApp(int coins){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);//android.content.
        sharingIntent.setType("text/plain");
        String shareBody = "I got "+ coins +" coins in Spinner Wheel! It is really great! https://play.google.com/store/apps/details?id=com.fingersoft.hillclimb";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Coins in Spinner Wheel!");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
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

//        if(redirected) {
//
//            timeOff = (System.currentTimeMillis() - date_firstLaunch) / 1000;
//
//            Log.i("Info", "You were gone for" + (System.currentTimeMillis() - date_firstLaunch) / 1000 + " sec");
//
//            if (timeOff >= 60) {
//
//                //Add your Point here
//                addTotalPoints(1000);
//
//                Log.i("Info", "You got the points");
//
//                MainActivity.prefEditor.putBoolean("isRated",true);
//
//            }
//
//            redirected = false;
//        }

        Log.i("Info","Resumed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
                buttonWatchVideo.setVisibility(View.VISIBLE);
//                Toast.makeText(context, "Loaded!!!!", Toast.LENGTH_SHORT).show();
            }else{
//                Toast.makeText(context, "Loaded but ... not yet", Toast.LENGTH_SHORT).show();
                buttonWatchVideo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        loadRewardedVideoAd();
//        Toast.makeText(context, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
//        Toast.makeText(context, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
//        Toast.makeText(context, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
//        Toast.makeText(context, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        if (MainActivity.sPref.getBoolean("isSpinnerBlocked", false)){
            isReward = true;
            unlockSpinner();
        }
        loadRewardedVideoAd();
//        Toast.makeText(context, "onRewarded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
//        Toast.makeText(context, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
//        Toast.makeText(context, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
//        Toast.makeText(context, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(context, "Points was successfully added", Toast.LENGTH_SHORT).show();

        totalPoints = 0;
        MainActivity.prefEditor.putInt("userTotalPoints", totalPoints).apply();
        textTotalPoints.setText(""+totalPoints);
    }

//    @Override
//    public void onBackPressed() {
//
//        layoutWarningClose.startAnimation(boardScale);
//        layoutWarningClose.setVisibility(View.VISIBLE);
//
//    }

}
