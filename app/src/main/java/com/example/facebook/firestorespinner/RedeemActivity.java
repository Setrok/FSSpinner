package com.example.facebook.firestorespinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.google.firebase.auth.FirebaseAuth;

public class RedeemActivity extends AppCompatActivity implements ScoreManager.IreedemActivityHandler{

    Button btnOk,btnCancel;
    Button btnTest;
    EditText etPaytmNumber,etAmount;
    ProgressBar progressBar;

    private static final double MIN_AMOUNT = 1000;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        mAuth = FirebaseAuth.getInstance();

        etPaytmNumber = findViewById(R.id.redeem_paytm_field);
        etAmount = findViewById(R.id.redeem_amount_field);

        btnOk = findViewById(R.id.redeem_confirm_btn);
        btnCancel = findViewById(R.id.redeem_cancel_btn);

        progressBar = findViewById(R.id.redeem_progressBar);
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

        btnTest = findViewById(R.id.redeem_test_addScore);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScoreManager.addScore(mAuth.getCurrentUser().getUid(),1000,"Test",true,true);
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
        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
    }

    private void sendData() {

        if(!TextUtils.isEmpty(etPaytmNumber.getText()) && !TextUtils.isEmpty(etAmount.getText())){

            double amount =  Double.parseDouble(etAmount.getText().toString());
            double number =  Double.parseDouble(etPaytmNumber.getText().toString());

            if(amount>=MIN_AMOUNT){

                ScoreManager.deductScore(number,mAuth.getCurrentUser().getUid(),amount,this);

            } else {
                Toast.makeText(getApplicationContext(),"Min Amount is "+ MIN_AMOUNT,Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Empty fields!",Toast.LENGTH_LONG).show();
        }

    }

    private void cancel(){
        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(mainIntent);
    }




}
