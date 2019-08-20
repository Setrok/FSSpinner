package com.kitecoin.free.quizmania.screens.redeem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kitecoin.free.quizmania.FireStore.ScoreManager;
import com.kitecoin.free.quizmania.FireStore.Users;
import com.kitecoin.free.quizmania.LoginActivity;
import com.kitecoin.free.quizmania.MainActivity;
import com.kitecoin.free.quizmania.R;
import com.kitecoin.free.quizmania.screens.home.HomeFragment;
import com.kitecoin.free.quizmania.utils.NetworkConnection;
import com.kitecoin.free.quizmania.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RedeemFragment extends Fragment implements ScoreManager.IreedemActivityHandler{

    View view;
    Context context;

    private static FragmentManager fragmentManager;

    ConstraintLayout layoutItemPaytm;
    ConstraintLayout layoutPopupPaytm;
    LinearLayout layoutResultPopup;

    ImageView ivResultIcon;

    TextView tvResult;

    Button btnOk,btnCancel, btnResultOK;
    EditText etPaytmNumber,etAmount;
    ProgressBar progressBar;

    //Animation
    Animation boardScale;

    private static final int MIN_AMOUNT = 1000;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_redeem, container, false);
        initRedeem();
        return view;
    }

    private void initRedeem() {


        mAuth = FirebaseAuth.getInstance();

        context = getActivity();

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        //Animation
        boardScale = AnimationUtils.loadAnimation(context, R.anim.anim_scale);

        layoutItemPaytm = view.findViewById(R.id.layout_pay_item_paytm);
        layoutPopupPaytm = view.findViewById(R.id.layout_pay_popup);
        layoutResultPopup = view.findViewById(R.id.layout_pay_result_popup);

        ivResultIcon = view.findViewById(R.id.iv_result_icon);

        tvResult = view.findViewById(R.id.tv_result_text);

        btnResultOK = view.findViewById(R.id.button_popup_ok);

        etPaytmNumber = view.findViewById(R.id.redeem_paytm_field);
        etAmount = view.findViewById(R.id.redeem_amount_field);

        btnOk = view.findViewById(R.id.redeem_confirm_btn);
        btnCancel = view.findViewById(R.id.redeem_cancel_btn);

        progressBar = view.findViewById(R.id.redeem_progressBar);

        layoutItemPaytm.setVisibility(View.GONE);
        layoutResultPopup.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        layoutPopupPaytm.setVisibility(View.VISIBLE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                }

                showProgressBar(true);
                layoutPopupPaytm.setVisibility(View.GONE);
                sendData();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        layoutItemPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutItemPaytm.setVisibility(View.GONE);

                layoutPopupPaytm.startAnimation(boardScale);
                layoutPopupPaytm.setVisibility(View.VISIBLE);

            }
        });

        btnResultOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkConnection.networkAvailable(context)) {
                    onNoInternetConnection();
                }

                layoutResultPopup.setVisibility(View.GONE);
                layoutItemPaytm.setVisibility(View.GONE);

                layoutPopupPaytm.startAnimation(boardScale);
                layoutPopupPaytm.setVisibility(View.VISIBLE);
            }
        });

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
    public void showProgressBar(boolean show) {

        if(show){
            progressBar.setVisibility(View.VISIBLE);
            btnOk.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnOk.setEnabled(true);
        }

    }

    @Override
    public void displayMessage(String error) {
        Toast.makeText(context,error,Toast.LENGTH_LONG).show();

        if (error.equals("Data is successfully sent for processing")){
            ivResultIcon.setImageResource(R.drawable.ic_done_black_24dp);
            tvResult.setText(error);
        } else {
            ivResultIcon.setImageResource(R.drawable.ic_close_black_24dp);
            tvResult.setText(error);
        }

        showProgressBar(false);

        layoutResultPopup.startAnimation(boardScale);
        layoutResultPopup.setVisibility(View.VISIBLE);

    }

    private void sendData() {

        if(!NetworkConnection.networkAvailable(context)){
            return;
        }

        if(!TextUtils.isEmpty(etPaytmNumber.getText()) && !TextUtils.isEmpty(etAmount.getText())){

            double amount =  Double.parseDouble(etAmount.getText().toString());
            String number =  etPaytmNumber.getText().toString();

            if(amount>=MIN_AMOUNT){

                ScoreManager.deductScore(number,mAuth.getCurrentUser().getUid(),amount,this);

            } else {

                showProgressBar(false);

//                Toast.makeText(getContext(),"Min Amount is "+ MIN_AMOUNT,Toast.LENGTH_LONG).show();

                displayMessage("Min Amount is "+MIN_AMOUNT);

//                layoutPopupPaytm.startAnimation(boardScale);
//                layoutPopupPaytm.setVisibility(View.VISIBLE);

            }
        } else {

            showProgressBar(false);

            Toast.makeText(getContext(),"Empty fields!",Toast.LENGTH_LONG).show();

            layoutPopupPaytm.startAnimation(boardScale);
            layoutPopupPaytm.setVisibility(View.VISIBLE);

        }

    }

    private void cancel(){

        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new HomeFragment(),
                        Utils.UHomeFragment).commit();

//        layoutPopupPaytm.setVisibility(View.GONE);
//        layoutItemPaytm.setVisibility(View.VISIBLE);

    }

}
