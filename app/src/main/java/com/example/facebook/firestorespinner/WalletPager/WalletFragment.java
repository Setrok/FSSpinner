package com.example.facebook.firestorespinner.WalletPager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.example.facebook.firestorespinner.R;
import com.google.firebase.auth.FirebaseAuth;


public class WalletFragment extends Fragment implements ScoreManager.IscoreDisplay{

    private View mainView;

    private ViewPager mViewPager;
    private PagerSectionsAdapter mPagerSectionsAdapter;
    private TabLayout mTabLayout;

    private TextView tvBalance;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_wallet, container, false);

        mAuth = FirebaseAuth.getInstance();

        initScoreLayout();

        initTabLayout();

        return mainView;
    }

    private void initScoreLayout() {

        tvBalance = mainView.findViewById(R.id.wallet_tvBalance);

        ScoreManager.getScore(mAuth.getCurrentUser().getUid(),this);

    }

    private void initTabLayout() {

        mViewPager = mainView.findViewById(R.id.main_pager);
        mPagerSectionsAdapter = new PagerSectionsAdapter(getChildFragmentManager());

        mViewPager.setAdapter(mPagerSectionsAdapter);

        mTabLayout = mainView.findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void setScore(long i) {

        String score  = i+"";

        tvBalance.setText(score);

    }

    @Override
    public void displayError(String error) {
        Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
    }
}
