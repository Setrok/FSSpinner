package com.kitecoin.free.quizmania.screens.invite;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kitecoin.free.quizmania.FireStore.Users;
import com.kitecoin.free.quizmania.R;
import com.google.firebase.auth.FirebaseAuth;


public class InviteFragment extends Fragment implements Users.IhandlCounter,View.OnClickListener{

    View mainView;
    TextView tvInviteCode;
    FirebaseAuth mAuth;
    AppCompatImageView btnCopyCode,btnShareCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_invite, container, false);

        initRefScreen();

        return mainView;
    }

    private void initRefScreen(){

        mAuth = FirebaseAuth.getInstance();

        tvInviteCode = mainView.findViewById(R.id.invite_refCode);

        btnCopyCode = mainView.findViewById(R.id.invite_copyCode);
        btnCopyCode.setOnClickListener(this);
        btnShareCode = mainView.findViewById(R.id.invite_shareCode);
        btnShareCode.setOnClickListener(this);

        Users.getUserCounter(mAuth.getCurrentUser().getUid(),this);

    }

    @Override
    public void getCounter(long counter) {

        String counterStr = counter+"";

        tvInviteCode.setText(counterStr);

    }

    @Override
    public void displayError(String e) {

        Toast.makeText(getContext(),e,Toast.LENGTH_LONG);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        String refCode = tvInviteCode.getText().toString();

        if (i == R.id.invite_copyCode) {

            try {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ref code", refCode);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Referral code copied Successfully", Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Toast.makeText(getContext(), "Error copying text", Toast.LENGTH_LONG).show();
            }
        }
        else if( i == R.id.invite_shareCode){

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Hello, \n" +
                    "\n" +
                    "I am using this application and play the quiz. It is Very easy to use and absolutely free.\n" +
                    "\n" +
                    "Please click below link to join with me.\n" +
                    "https://play.google.com/store/apps/details?id=com.tilseier.switchitpennywise\n" +
                    "\n" +
                    "My referral code is "+ refCode +"\n" +
                    "\n" +
                    "Thanks";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share code");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }
    }
}
