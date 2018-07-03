package com.example.facebook.firestorespinner.screens.quiz;

public interface IQuiz {

    interface View{

        void disableQuiz();
        void enableQuiz();
        void uncheckQuiz();
        void initTimer();
        void startTimer();
        void stopTimer();
        void continueTimer();
        void setCorrectScore(int score);
        void setWrongScore(int score);
        void setQuestion(String question);
        void setAnswer1(int answer);
        void setAnswer2(int answer);
        void setAnswer3(int answer);
        int getUserAnswer();
        void showPopup(int icon, int text, int button);
        void hidePopup();
        void showToast(String str);
        void showInterstitial();

    }

    interface Presenter{

        void onCreate();
        void onStart();
        void onPopupOkClick();
        void onTimerFinished();
        void onResume();
        void onPause();
        void onAdOpened();
        void onAdClosed();
        void onAdLeftApplication();

    }

    interface Module{

        void addPointsToDB(int points);

    }


}
