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

import com.example.facebook.firestorespinner.R;


public class WalletFragment extends Fragment {

    private View mainView;

    private ViewPager mViewPager;
    private PagerSectionsAdapter mPagerSectionsAdapter;
    private TabLayout mTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_wallet, container, false);

        initTabLayout();

        return mainView;
    }

    private void initTabLayout() {

        mViewPager = mainView.findViewById(R.id.main_pager);
        mPagerSectionsAdapter = new PagerSectionsAdapter(getActivity().getSupportFragmentManager());

        mViewPager.setAdapter(mPagerSectionsAdapter);

        mTabLayout = mainView.findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

}
