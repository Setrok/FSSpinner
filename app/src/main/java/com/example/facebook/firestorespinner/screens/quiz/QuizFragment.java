package com.example.facebook.firestorespinner.screens.quiz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.facebook.firestorespinner.R;

public class QuizFragment extends Fragment implements IQuiz.View {

    private View view;
    private Context context;

    LinearLayout layoutResultPopup;

    Button btnPopupOK;

    //Animation
    Animation boardScale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quiz, container, false);
        initQuiz();
        return view;
    }

    private void initQuiz() {

        context = getActivity();

        layoutResultPopup = view.findViewById(R.id.layout_result_popup);

        //Buttons
        btnPopupOK =  view.findViewById(R.id.button_popup_ok);

        //Animation
        boardScale = AnimationUtils.loadAnimation(context, R.anim.anim_scale);

        final IQuiz.Presenter presenter = new QuizPresenter(this);
        presenter.onCreate();


        btnPopupOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPopupOkClick();
            }
        });

    }

    @Override
    public void disableQuiz() {

    }

    @Override
    public void enableQuiz() {

    }

    @Override
    public void updateTimer(int time) {

    }

    @Override
    public void setCorrectScore(int score) {

    }

    @Override
    public void setWrongScore(int score) {

    }

    @Override
    public int getUserAnswer() {
        return 0;
    }

    @Override
    public void showPopup() {
        layoutResultPopup.startAnimation(boardScale);
        layoutResultPopup.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePopup() {
        layoutResultPopup.setVisibility(View.GONE);
    }
}
