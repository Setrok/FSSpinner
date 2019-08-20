package com.kitecoin.free.quizmania.screens.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kitecoin.free.quizmania.R;
import com.kitecoin.free.quizmania.WalletPager.WalletFragment;
import com.kitecoin.free.quizmania.screens.invite.InviteFragment;
import com.kitecoin.free.quizmania.screens.my_team.MyTeamFragment;
import com.kitecoin.free.quizmania.screens.playspin.PlaySpinFragment;
import com.kitecoin.free.quizmania.screens.quiz.QuizFragment;
import com.kitecoin.free.quizmania.screens.redeem.RedeemFragment;
import com.kitecoin.free.quizmania.utils.NetworkConnection;
import com.kitecoin.free.quizmania.utils.Utils;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    Context context;

    View view;
    private static FragmentManager fragmentManager;

    ConstraintLayout clMenuItem1;
    ConstraintLayout clMenuItem2;
    ConstraintLayout clMenuItem3;
    ConstraintLayout clMenuItem4;
    ConstraintLayout clMenuItem5;
    ConstraintLayout clMenuItem6;
    ConstraintLayout clMenuItem7;
    ConstraintLayout clMenuItem8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initHome();
        return view;
    }

    private void initHome() {

        context = getActivity();

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        clMenuItem1 = view.findViewById(R.id.clMenuItem1);
        clMenuItem2 = view.findViewById(R.id.clMenuItem2);
        clMenuItem3 = view.findViewById(R.id.clMenuItem3);
        clMenuItem4 = view.findViewById(R.id.clMenuItem4);
        clMenuItem5 = view.findViewById(R.id.clMenuItem5);
        clMenuItem6 = view.findViewById(R.id.clMenuItem6);
        clMenuItem7 = view.findViewById(R.id.clMenuItem7);
        clMenuItem8 = view.findViewById(R.id.clMenuItem8);

        clMenuItem1.setOnClickListener(buttonClickListener);
        clMenuItem2.setOnClickListener(buttonClickListener);
        clMenuItem3.setOnClickListener(buttonClickListener);
        clMenuItem4.setOnClickListener(buttonClickListener);
        clMenuItem5.setOnClickListener(buttonClickListener);
        clMenuItem6.setOnClickListener(buttonClickListener);
        clMenuItem7.setOnClickListener(buttonClickListener);
        clMenuItem8.setOnClickListener(buttonClickListener);

    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            if (!NetworkConnection.networkAvailable(getContext())) {
                Toast.makeText(context, "No internet Connection", Toast.LENGTH_LONG).show();
                return;
            }


            switch (v.getId()) {
                case R.id.clMenuItem1:
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new PlaySpinFragment(),
                                    Utils.UPlaySpinFragment).commit();
                    break;
                case R.id.clMenuItem2:
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new QuizFragment(),
                                    Utils.UQuizFragment).commit();
                    break;
                case R.id.clMenuItem3:
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new WalletFragment(),
                                    Utils.UWalletFragment).commit();
                    break;
                case R.id.clMenuItem4:
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new RedeemFragment(),
                                    Utils.URedeemFragment).commit();
                    break;
                case R.id.clMenuItem5:
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new InviteFragment(),
                                    Utils.UInviteFragment).commit();
                    break;
                case R.id.clMenuItem6:
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                            .replace(R.id.frameContainer, new MyTeamFragment(),
                                    Utils.UMyTeamFragment).commit();
                    break;
                case R.id.clMenuItem7:

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/html");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "test@gmail.com"});
                    final PackageManager pm = context.getPackageManager();
                    final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                    String className = null;
                    for (final ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.equals("com.google.android.gm")) {
                            className = info.activityInfo.name;

                            if(className != null && !className.isEmpty()){
                                break;
                            }
                        }
                    }

                    emailIntent.setClassName("com.google.android.gm", className);

                    try {
                        startActivity(emailIntent);
                    } catch(ActivityNotFoundException ex) {
                        // handle error
                    }

                    break;
                case R.id.clMenuItem8:
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.tilseier.switchitpennywise")));
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.tilseier.switchitpennywise")));
                    }
                    break;
            }
        }
    };


}
