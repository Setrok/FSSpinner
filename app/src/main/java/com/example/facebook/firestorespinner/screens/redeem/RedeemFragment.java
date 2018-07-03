package com.example.facebook.firestorespinner.screens.redeem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.example.facebook.firestorespinner.FireStore.Users;
import com.example.facebook.firestorespinner.LoginActivity;
import com.example.facebook.firestorespinner.MainActivity;
import com.example.facebook.firestorespinner.R;
import com.example.facebook.firestorespinner.utils.NetworkConnection;
import com.google.firebase.auth.FirebaseAuth;

public class RedeemFragment extends Fragment implements ScoreManager.IreedemActivityHandler{

    View view;

    Button btnOk,btnCancel;
    Button btnTest;
    EditText etPaytmNumber,etAmount;
    ProgressBar progressBar;

    Context context;

    private static final double MIN_AMOUNT = 1000;

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

        etPaytmNumber = view.findViewById(R.id.redeem_paytm_field);
        etAmount = view.findViewById(R.id.redeem_amount_field);

        btnOk = view.findViewById(R.id.redeem_confirm_btn);
        btnCancel = view.findViewById(R.id.redeem_cancel_btn);

        progressBar = view.findViewById(R.id.redeem_progressBar);
        progressBar.setVisibility(View.GONE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar(true);
                sendData();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        btnTest = view.findViewById(R.id.redeem_test_addScore);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ScoreManager.addScore(mAuth.getCurrentUser().getUid(),1000,"Test",true,true);
            }
        });


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
    }

    private void sendData() {

        if(!NetworkConnection.networkAvailable(context)){
            return;
        }

        if(!TextUtils.isEmpty(etPaytmNumber.getText()) && !TextUtils.isEmpty(etAmount.getText())){

            double amount =  Double.parseDouble(etAmount.getText().toString());
            double number =  Double.parseDouble(etPaytmNumber.getText().toString());

            if(amount>=MIN_AMOUNT){

                ScoreManager.deductScore(number,mAuth.getCurrentUser().getUid(),amount,this);

            } else {
                Toast.makeText(getContext(),"Min Amount is "+ MIN_AMOUNT,Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(),"Empty fields!",Toast.LENGTH_LONG).show();
        }

    }

    private void cancel(){
        Intent mainIntent = new Intent(getContext(),MainActivity.class);
        startActivity(mainIntent);
    }

}
