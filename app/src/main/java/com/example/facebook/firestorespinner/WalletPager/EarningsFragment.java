package com.example.facebook.firestorespinner.WalletPager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facebook.firestorespinner.R;


public class EarningsFragment extends Fragment {

    View mainView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView  = inflater.inflate(R.layout.fragment_earnings, container, false);

        return mainView;
    }

}
