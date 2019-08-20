package com.kitecoin.free.quizmania.WalletPager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kitecoin.free.quizmania.FireStore.ScoreManager;
import com.kitecoin.free.quizmania.R;
import com.kitecoin.free.quizmania.screens.home.HomeFragment;
import com.kitecoin.free.quizmania.screens.redeem.RedeemFragment;
import com.kitecoin.free.quizmania.utils.NetworkConnection;
import com.kitecoin.free.quizmania.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class WalletFragment extends Fragment implements ScoreManager.IscoreDisplay{

    private View mainView;
    private Context context;

    private static FragmentManager fragmentManager;

    private LinearLayout layoutRedeemNavigation;

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

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        initScoreLayout();

        initTabLayout();

        return mainView;
    }

    private void initScoreLayout() {

        context = getActivity();

        tvBalance = mainView.findViewById(R.id.wallet_tvBalance);

        layoutRedeemNavigation = mainView.findViewById(R.id.layout_redeem_navigation);

        ScoreManager.getScore(mAuth.getCurrentUser().getUid(),this);

        layoutRedeemNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                } else {
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new RedeemFragment(),
                                    Utils.URedeemFragment).commit();
                }

            }
        });

    }

    private void initTabLayout() {

        mViewPager = mainView.findViewById(R.id.main_pager);
        mPagerSectionsAdapter = new PagerSectionsAdapter(getChildFragmentManager());

        mViewPager.setAdapter(mPagerSectionsAdapter);

        mTabLayout = mainView.findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void onNoInternetConnection(){

        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new HomeFragment(),
                        Utils.UHomeFragment).commit();

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
