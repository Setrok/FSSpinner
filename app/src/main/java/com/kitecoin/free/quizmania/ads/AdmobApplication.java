package com.kitecoin.free.quizmania.ads;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by TilSeier on 13.12.2017.
 */

public class AdmobApplication {
    public static InterstitialAd mInterstitialAd;
    public static boolean isLoadedAdmob = false;

    private static final String mobileAdsAppID = "ca-app-pub-3940256099942544~3347511713";

    public static void createWallAd(Context context){

        if(!isLoadedAdmob) {

            MobileAds.initialize(context, mobileAdsAppID);

            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

            isLoadedAdmob = true;

        }

    }
    public static void requestNewInterstitial() {
        if (!isAdLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .addTestDevice("EC07F4759620B8F1E3BD5F493490BEB4")
                    .build());
        }

        //.addTestDevice("EC07F4759620B8F1E3BD5F493490BEB4")
        //                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
    }
    public static boolean isAdLoaded(){
        if (mInterstitialAd.isLoaded()) {
            return true;
        }
        return false;
    }
    public static void displayLoadedAd(){
        mInterstitialAd.show();
    }

}