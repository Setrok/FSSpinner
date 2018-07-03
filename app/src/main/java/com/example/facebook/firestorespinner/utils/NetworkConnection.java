package com.example.facebook.firestorespinner.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.Users;

public class NetworkConnection {

//    static NetworkConnection instance;
//
//    private NetworkConnection (){
//    }
//
//    public static NetworkConnection getInstance(){
//        if(null==instance)
//            instance = new NetworkConnection();
//        return instance;
//    }


    public static boolean networkAvailable(Context context) {
        try {
            boolean wifiDataAvailable = false;
            boolean mobileDataAvailable = false;
            ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
            for (NetworkInfo netInfo : networkInfo) {
                if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                    if (netInfo.isConnected())
                        wifiDataAvailable = true;
                if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (netInfo.isConnected())
                        mobileDataAvailable = true;
            }
            return wifiDataAvailable || mobileDataAvailable;
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
