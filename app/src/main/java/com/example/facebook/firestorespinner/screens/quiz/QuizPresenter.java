package com.example.facebook.firestorespinner.screens.quiz;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.facebook.firestorespinner.FireStore.ScoreManager;
import com.example.facebook.firestorespinner.MainActivity;
import com.example.facebook.firestorespinner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Random;

public class QuizPresenter implements IQuiz.Presenter,ScoreManager.IscoreMessage {

    private String TAG = "QuizPresenter";

    private IQuiz.View view;
    private Random rand;

    private int WIN_AFTER = 13;
    private int WIN_TIMER_AD = 4000;
    private int DAY_QUIZ_LIMIT = 8;

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

        checkForLimit();

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
    public void onDailyLimitOkClick() {
        view.goBack();
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









    //Daily Limit

    public void checkForLimit(){

        //prefs.edit().putInt("DayOfReward", 0).apply();//to be commented
        //prefs.edit().putLong("DailyReward", 0).apply();

//        if( prefs.getInt("DayOfReward",-1) == -1){
//
//            prefs.edit().putInt("DayOfReward", 0).apply();
//        }

        Log.i("GLOBALPREF", "DayQuizLimit="+MainActivity.sPref.getInt("DayQuizLimit", 0));
        Log.i("GLOBALPREF", "DayQuizLimitTime="+MainActivity.sPref.getLong("DayQuizLimitTime", 0));

        if (MainActivity.sPref.getLong("DayQuizLimitTime",0) == 0){
            setDateToZero();
        }

        if((System.currentTimeMillis() - MainActivity.sPref.getLong("DayQuizLimitTime",0) ) / 1000 >=  3600*24){//3600*24

            Log.i("GLOBALPREF", "ToZERO="+(System.currentTimeMillis() - MainActivity.sPref.getLong("DayQuizLimitTime",0) ) / 1000);

            MainActivity.prefEditor.putInt("DayQuizLimit",0).apply();
            setDateToZero();
            view.hideBlockQuiz();

        } else {

            if(MainActivity.sPref.getInt("DayQuizLimit",0) >= DAY_QUIZ_LIMIT){

                view.disableQuiz();
                view.stopTimer();
                view.showBlockQuiz();
                view.hidePopup();

            }else {
                view.hideBlockQuiz();
            }

        }

//        if(MainActivity.sPref.getInt("DayQuizLimit",0) >= DAY_QUIZ_LIMIT){
//
//                if((System.currentTimeMillis() - MainActivity.sPref.getLong("DayQuizLimitTime",0) ) / 1000 >=  60*20){//3600*24
//
//                    MainActivity.prefEditor.putInt("DayQuizLimit",0).apply();
//                    setDateToZero();
//                    view.hideBlockQuiz();
//
//                } else {
//
//                    view.disableQuiz();
//                    view.stopTimer();
//                    view.showBlockQuiz();
//                    view.hidePopup();
//
//                }
//
//        }else {
//            view.hideBlockQuiz();
//        }



//        long prefExist = MainActivity.sPref.getLong("DailyReward",0);
//
//        if(prefExist == 0|| MainActivity.sPref.getInt("DayOfReward",0) == 0){
//
//            addDaytoPrefs();
//            addTotalPoints(10);
//            setDateToZero();
//            showDailyReward();
////            Toast.makeText(GameActivity.this,"First reward",Toast.LENGTH_LONG).show();
//
//        }
//        //setDateToZero();
//
//
////Just in time //3600*24 //3600*48
//        if( (System.currentTimeMillis() - MainActivity.sPref.getLong("DailyReward",0) ) / 1000 >= 3600*24
//                && (System.currentTimeMillis() - MainActivity.sPref.getLong("DailyReward",0) ) / 1000 <=  3600*48)
//        {
//            addDaytoPrefs();
//            setDateToZero();
//
//            showDailyReward();
//            addTotalPoints(MainActivity.sPref.getInt("DayOfReward",1)*10);
//
////            Toast.makeText(GameActivity.this,"Get your reward, day:"+MainActivity.sPref.getInt("DayOfReward",0),Toast.LENGTH_LONG).show();
////Too late loser
//        } else if((System.currentTimeMillis() - MainActivity.sPref.getLong("DailyReward",0) ) / 1000 >=  3600*48){
//
//            MainActivity.prefEditor.putInt("DayOfReward",1).apply();
//            setDayImages(1);
//
//            showDailyReward();
//
//            addTotalPoints(10);
//
//            setDateToZero();
////            Toast.makeText(GameActivity.this,"Missed reward, get first"+MainActivity.sPref.getInt("DayOfReward",0),Toast.LENGTH_LONG).show();
////Too early
//        } else {
//            setDayImages(MainActivity.sPref.getInt("DayOfReward",0));
////            Toast.makeText(GameActivity.this,"Too early " + MainActivity.sPref.getInt("DayOfReward",0),Toast.LENGTH_LONG).show();
//        }

    }

//    private void addDaytoPrefs() {
//        int currentDayOfReward = MainActivity.sPref.getInt("DayOfReward",0);
//
//        if(currentDayOfReward<6) {
//            MainActivity.prefEditor.putInt("DayOfReward", (currentDayOfReward + 1)).apply();
//            setDayImages(currentDayOfReward + 1);
//        }
//        else {
//            MainActivity.prefEditor.putInt("DayOfReward", 1).apply();
//            setDayImages(1);
//        }
//
//    }

//    private void setDayImages(int currentDayOfReward) {
//
//        Log.i("Info","current images unlocked"+ (currentDayOfReward+1));
//        for(int i = 0;i<6;i++){
//
//            if(i<currentDayOfReward)
//                imageDayRewards[i].setImageResource(images[i]);
//            else imageDayRewards[i].setImageResource(images[6]);
//
//        }
//
//    }

    private void setDateToZero() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int sec = Calendar.getInstance().get(Calendar.SECOND);

        Log.i("Info","hour is" + hour + "min is" + min + "sec is" + sec);
        MainActivity.prefEditor.putLong("DayQuizLimitTime", System.currentTimeMillis()- hour*3600*1000 - min*1000*60 - sec*1000).apply();

//        MainActivity.prefEditor.putLong("DailyReward", System.currentTimeMillis()).apply();

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

        checkForLimit();

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

        int currentLimit = MainActivity.sPref.getInt("DayQuizLimit",0);

        MainActivity.prefEditor.putInt("DayQuizLimit", (currentLimit + 1)).apply();

//        Log.i("GLOBALPREF", "sP="+MainActivity.sPref.getInt("DayQuizLimit", 0));

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