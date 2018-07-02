package com.example.facebook.firestorespinner.screens.quiz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facebook.firestorespinner.R;

public class QuizFragment extends Fragment {

    private View view;
    private Context context;

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





    }

}
