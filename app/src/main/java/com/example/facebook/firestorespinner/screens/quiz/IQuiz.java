package com.example.facebook.firestorespinner.screens.quiz;

public interface IQuiz {

    interface View{

        void disableQuiz();
        void enableQuiz();
        void updateTimer(int time);
        void setCorrectScore(int score);
        void setWrongScore(int score);
        int getUserAnswer();
        void showPopup();
        void hidePopup();

    }

    interface Presenter{

        void onCreate();
        void onStart();
        void onPopupOkClick();

    }

    interface Module{

        void addPointsToDB(int points);

    }


}
