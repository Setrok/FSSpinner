package com.example.facebook.firestorespinner.screens.quiz;

public interface IQuiz {

    interface View{

        void updateTimer(int time);
        void setCorrectScore(int score);
        void setWrongScore(int score);
        int getUserAnswer();

    }

    interface Presenter{

        void onCreate();
        void onStart();

    }

    interface Module{

        void addPointsToDB(int points);

    }


}
