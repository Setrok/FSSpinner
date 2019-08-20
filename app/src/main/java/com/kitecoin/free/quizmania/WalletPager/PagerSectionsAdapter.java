package com.kitecoin.free.quizmania.WalletPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Artem on 16.01.2018.
 */

class PagerSectionsAdapter extends FragmentPagerAdapter {


    public PagerSectionsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                EarningsFragment requestFragment = new EarningsFragment();
                return requestFragment;

            case 1:
                WithdrawFragment chatsFragment = new WithdrawFragment();
                return chatsFragment;


            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return 2; //Num of Tabs
    }

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Earnings";

            case 1:
                return "Withdraw";


            default:
                return "";
        }

    }

}
