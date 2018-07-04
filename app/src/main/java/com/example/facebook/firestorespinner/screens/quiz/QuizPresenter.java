package com.example.facebook.firestorespinner.screens.quiz;

import android.os.Handler;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.example.facebook.firestorespinner.MainActivity;
import com.example.facebook.firestorespinner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class QuizPresenter implements IQuiz.Presenter,ScoreManager.IscoreMessage {

    private String TAG = "QuizPresenter";

    private IQuiz.View view;
    private Random rand;

    private int WIN_AFTER = 2;
    private int WIN_TIMER_AD = 4000;

    private Handler adTimer;

    //Game State
    private enum GameState { PLAY, SHOW_RESULT };
    private GameState currentGameState;

    private boolean afterOnPause = false;

    private int correctAnswersCount = 0;
    private int wrongAnswersCount = 0;

    private String currentSing = "+";
    private int currentQuestionPart1, currentQuestionPart2;
    private int currentAnswer1;
    private int currentAnswer2;
    private int currentAnswer3;

    private int userAnswer;
    private int correctAnswer;
    private int uncheckAnswer = -1;

    private int RANGE_FROM = 1;
    private int RANGE_TO = 1000;

    private int RANGE_FROM_ERROR = 50;
    private int RANGE_TO_ERROR = 300;

    private FirebaseAuth mAuth;
    QuizPresenter(IQuiz.View view){
        this.view = view;
    }

    @Override
    public void onCreate() {

        mAuth = FirebaseAuth.getInstance();

        currentGameState = GameState.PLAY;

        correctAnswersCount = MainActivity.sPref.getInt("quizCorrectAnswersCount", 0);
        wrongAnswersCount = MainActivity.sPref.getInt("quizWrongAnswersCount", 0);
        view.setCorrectScore(correctAnswersCount);
        view.setWrongScore(wrongAnswersCount);

        rand = new Random();

        adTimer = new Handler();

        initQuestion();

        view.enableQuiz();
        view.uncheckQuiz();
        view.initTimer();
        view.startTimer();

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPopupOkClick() {

        if (userAnswer != uncheckAnswer) {

            if (userAnswer == correctAnswer) {
                correctAnswersCount++;
                view.setCorrectScore(correctAnswersCount);
            } else {
                wrongAnswersCount++;
                view.setWrongScore(wrongAnswersCount);
            }

        }

        initQuestion();

        view.enableQuiz();
        view.uncheckQuiz();
        view.hidePopup();
        view.startTimer();

        currentGameState = GameState.PLAY;

    }

    @Override
    public void onTimerFinished() {

        if (!afterOnPause) {

            view.disableQuiz();

            view.showInterstitial();

            currentGameState = GameState.SHOW_RESULT;

            userAnswer = view.getUserAnswer();

            if (userAnswer != uncheckAnswer) {

                if (userAnswer == correctAnswer) {
                    view.showPopup(R.drawable.ic_done_black_24dp, R.string.answer_is_correct, R.string.text_ok);
                } else {
                    view.showPopup(R.drawable.ic_close_black_24dp, R.string.answer_is_wrong, R.string.text_ok);
                }

            } else {

                view.showPopup(R.drawable.ic_close_black_24dp, R.string.no_answer, R.string.text_try_again);

                //Animation with Interval
//            final Handler h = new Handler();
//            h.postDelayed(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//
//                    view.uncheckQuiz();
//                    view.enableQuiz();
//                    view.hidePopup();
//                    view.startTimer();
//
////                    h.postDelayed(this, 3000);
//                }
//            }, 1000); // 1 second delay (takes millis)

            }

        }else {
            afterOnPause = false;
        }

    }

    @Override
    public void onResume() {

//        view.showToast("onResume");

        afterOnPause = false;
        if (currentGameState != GameState.SHOW_RESULT)
            view.continueTimer();


        if ((correctAnswersCount + wrongAnswersCount) >= WIN_AFTER) {

            adTimer.removeCallbacksAndMessages(null);

        }

    }

    @Override
    public void onPause() {

//        view.showToast("onPause");

        MainActivity.prefEditor.putInt("quizCorrectAnswersCount", correctAnswersCount).apply();
        MainActivity.prefEditor.putInt("quizWrongAnswersCount", wrongAnswersCount).apply();

        afterOnPause = true;
        if (currentGameState != GameState.SHOW_RESULT)
            view.stopTimer();
    }

    @Override
    public void onAdOpened() {

        if ((correctAnswersCount + wrongAnswersCount) >= WIN_AFTER){

            view.showToast("Click on Ad");

        }

    }

    @Override
    public void onAdClosed() {

    }

    @Override
    public void onAdLeftApplication() {

        if ((correctAnswersCount + wrongAnswersCount) >= WIN_AFTER) {

            adTimer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    ScoreManager.addScore(mAuth.getCurrentUser().getUid(),
                            80,"Quiz Bonus",true,true,QuizPresenter.this);
                    restartQuiz();

//                  adTimer.postDelayed(this, 3000);
                }
            }, WIN_TIMER_AD);

        }

    }

    private void initQuestion(){


        currentQuestionPart1 = rand.nextInt(RANGE_TO)+RANGE_FROM;
        currentQuestionPart2 = rand.nextInt(RANGE_TO)+RANGE_FROM;

        int currentResult = currentQuestionPart1 + currentQuestionPart2;

        correctAnswer = rand.nextInt(3)+1;

        if (correctAnswer == 1){
            currentAnswer1 = currentResult;

            if (rand.nextBoolean()){
                currentAnswer2 = currentResult + (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
            }else{
                currentAnswer2 = currentResult - (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
            }

            if (rand.nextBoolean()){
                do{
                    currentAnswer3 = currentResult + (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
                }while (currentAnswer3 == currentAnswer2);
            }else{
                do{
                    currentAnswer3 = currentResult - (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
                }while (currentAnswer3 == currentAnswer2);
            }

        }else if (correctAnswer == 2){

            currentAnswer2 = currentResult;

            if (rand.nextBoolean()){
                currentAnswer1 = currentResult + (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
            }else{
                currentAnswer1 = currentResult - (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
            }

            if (rand.nextBoolean()){
                do{
                    currentAnswer3 = currentResult + (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
                }while (currentAnswer3 == currentAnswer1);
            }else{
                do{
                    currentAnswer3 = currentResult - (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
                }while (currentAnswer3 == currentAnswer1);
            }

        }else {

            currentAnswer3 = currentResult;

            if (rand.nextBoolean()){
                currentAnswer1 = currentResult + (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
            }else{
                currentAnswer1 = currentResult - (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
            }

            if (rand.nextBoolean()){
                do{
                    currentAnswer2 = currentResult + (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
                }while (currentAnswer2 == currentAnswer1);
            }else{
                do{
                    currentAnswer2 = currentResult - (rand.nextInt(RANGE_TO_ERROR)+RANGE_FROM_ERROR);
                }while (currentAnswer2 == currentAnswer1);
            }

        }

        view.setQuestion(currentQuestionPart1+" "+currentSing+" "+currentQuestionPart2);
        view.setAnswer1(currentAnswer1);
        view.setAnswer2(currentAnswer2);
        view.setAnswer3(currentAnswer3);

    }

    private void restartQuiz(){

        correctAnswersCount = 0;
        wrongAnswersCount = 0;

        view.setCorrectScore(correctAnswersCount);
        view.setWrongScore(wrongAnswersCount);

    }

    @Override
    public void displayError(String error) {

//        view.showToast("Error adding scores");

    }

    @Override
    public void scoreAddSuccess() {
        view.showToast("Coins added successfully");
    }
}