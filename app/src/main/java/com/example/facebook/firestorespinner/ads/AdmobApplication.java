package com.example.facebook.firestorespinner.ads;

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

    private static final String mobileAdsAppID = "ca-app-pub-7508352159228516~6601953710";

    public static void createWallAd(Context context){

        if(!isLoadedAdmob) {

            MobileAds.initialize(context, mobileAdsAppID);

            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId("ca-app-pub-7508352159228516/4808282471");

            isLoadedAdmob = true;

        }

    }
    public static void requestNewInterstitial() {
        if (!isAdLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .build());
        }

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