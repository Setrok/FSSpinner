package com.example.facebook.firestorespinner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.Users;
import com.google.firebase.auth.FirebaseAuth;

public class ReferalActivity extends AppCompatActivity implements View.OnClickListener,Users.IhandleTransaction{

    EditText etCode;
    Button okBtn,skipBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);

        initInterface();

    }

    private void initInterface() {

        mAuth = FirebaseAuth.getInstance();

        etCode = findViewById(R.id.referal_codeFld);

        okBtn = findViewById(R.id.referal_okBtn);
        skipBtn = findViewById(R.id.referal_skipBtn);
        okBtn.setOnClickListener(this);
        skipBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.referal_okBtn){

            try {
                String codeStr = etCode.getText().toString();
                long code = Long.parseLong(codeStr);
                Users.checkIfRefExists(mAuth.getCurrentUser().getUid(),code,this);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(),"Wrong input format",Toast.LENGTH_LONG).show();
            }

        }
        else if(i == R.id.referal_skipBtn){
            loadActivity(0);
        }
    }

    @Override
    public void loadActivity(int i) {
        if(i==0){
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void getSpinsLeft(long spins, long userCounter, boolean redirectToReferal) {

    }

    @Override
    public void showProgressBar(boolean b) {

    }

    @Override
    public void displayError(String error) {
        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

    }
}
