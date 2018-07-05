package com.example.facebook.firestorespinner.screens.quiz;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facebook.firestorespinner.R;
import com.example.facebook.firestorespinner.ads.AdmobApplication;
import com.example.facebook.firestorespinner.screens.home.HomeFragment;
import com.example.facebook.firestorespinner.utils.Utils;
import com.google.android.gms.ads.AdListener;

import java.util.Objects;

public class QuizFragment extends Fragment implements IQuiz.View {

    private View view;
    private Context context;
    IQuiz.Presenter presenter;

    private static FragmentManager fragmentManager;

    //Layouts
    LinearLayout layoutResultPopup;
    LinearLayout layoutDailyLimitReached;

    //Buttons
    Button btnPopupOK;
    Button btnDailyLimitOk;

    //Animation
    Animation boardScale;

    //ProgressBar
    ProgressBar pbCircle;

    //TextView
    TextView tvWrongRes;
    TextView tvCorrectRes;
    TextView tvResultTextPopup;
    TextView tvQuestion;

    //ImageView
    ImageView ivResultIconPopup;

    //RadioGroup
    RadioGroup radioAnswersGroup;

    //RadioButton
    RadioButton rbAnswer1;
    RadioButton rbAnswer2;
    RadioButton rbAnswer3;

    //Continue Timer Line
    private ObjectAnimator animationTimer;
    private long currentMilliseconds = 0;
    private boolean isTimerCanceled = false;

    //
    int userAnswer = -1;

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

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        //Interstitial
        AdmobApplication.createWallAd(context);
        AdmobApplication.requestNewInterstitial();

        //Layouts
        layoutResultPopup = view.findViewById(R.id.layout_result_popup);
        layoutDailyLimitReached = view.findViewById(R.id.layout_daily_limit_reached);

        //Buttons
        btnPopupOK =  view.findViewById(R.id.button_popup_ok);
        btnDailyLimitOk =  view.findViewById(R.id.button_daily_limit_reached);

        //ProgressBar
        pbCircle = view.findViewById(R.id.progress_bar_time_line);

        //TextView
        tvWrongRes = view.findViewById(R.id.text_wrong_answers);
        tvCorrectRes = view.findViewById(R.id.text_correct_answers);
        tvResultTextPopup = view.findViewById(R.id.tv_result_text);
        tvQuestion = view.findViewById(R.id.tv_question_quiz);

        //ImageView
        ivResultIconPopup = view.findViewById(R.id.iv_result_icon);

        //Animation
        boardScale = AnimationUtils.loadAnimation(context, R.anim.anim_scale);

        //RadioGroup
        radioAnswersGroup = view.findViewById(R.id.radioGroupAnswers);

        //RadioButton
        rbAnswer1 = view.findViewById(R.id.answer1);
        rbAnswer2 = view.findViewById(R.id.answer2);
        rbAnswer3 = view.findViewById(R.id.answer3);


        presenter = new QuizPresenter(this);
        presenter.onCreate();


        btnPopupOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPopupOkClick();
            }
        });

        btnDailyLimitOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDailyLimitOkClick();
            }
        });

        radioAnswersGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        userAnswer = -1;
//                        Toast.makeText(context, "Nothing",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.answer1:
                        userAnswer = 1;
//                        Toast.makeText(context, "First",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.answer2:
                        userAnswer = 2;
//                        Toast.makeText(context, "Second",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.answer3:
                        userAnswer = 3;
//                        Toast.makeText(context, "Third",
//                                Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        });


    }

    @Override
    public void disableQuiz() {

//        radioAnswersGroup.setEnabled(false);

        rbAnswer1.setEnabled(false);
        rbAnswer2.setEnabled(false);
        rbAnswer3.setEnabled(false);
    }

    @Override
    public void enableQuiz() {

//        radioAnswersGroup.setEnabled(true);

        rbAnswer1.setEnabled(true);
        rbAnswer2.setEnabled(true);
        rbAnswer3.setEnabled(true);
    }

    @Override
    public void uncheckQuiz() {

        radioAnswersGroup.clearCheck();

//        rbAnswer1.setChecked(false);
//        rbAnswer2.setChecked(false);
//        rbAnswer3.setChecked(false);

        userAnswer = -1;

    }

    @Override
    public void initTimer() {

        //Timer Circle
        animationTimer = ObjectAnimator.ofInt(pbCircle, "progress", 100, 0);
        animationTimer.setDuration(10000);
        animationTimer.setInterpolator(new DecelerateInterpolator());
        animationTimer.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isTimerCanceled = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                Log.i("GLOBAL STEP", "EndAnimation");
                //do something when the countdown is complete
                presenter.onTimerFinished();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
//                Log.i("GLOBAL STEP", "CancelAnimation");
                isTimerCanceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });

    }

    @Override
    public void startTimer() {
        animationTimer.start();
    }

    @Override
    public void stopTimer() {
        currentMilliseconds = animationTimer.getCurrentPlayTime();
        animationTimer.cancel();
    }

    @Override
    public void continueTimer() {
        animationTimer.start();
        animationTimer.setCurrentPlayTime(currentMilliseconds);
    }

    @Override
    public void setCorrectScore(int score) {
        tvCorrectRes.setText(""+score);
    }

    @Override
    public void setWrongScore(int score) {
        tvWrongRes.setText(""+score);
    }

    @Override
    public void setQuestion(String question) {
        tvQuestion.setText(question);
    }

    @Override
    public void setAnswer1(int answer) {
        rbAnswer1.setText(""+answer);
    }

    @Override
    public void setAnswer2(int answer) {
        rbAnswer2.setText(""+answer);
    }

    @Override
    public void setAnswer3(int answer) {
        rbAnswer3.setText(""+answer);
    }

    @Override
    public int getUserAnswer() {
        return userAnswer;
    }

    @Override
    public void showPopup(int icon, int text, int button) {
        ivResultIconPopup.setImageResource(icon);
        tvResultTextPopup.setText(text);
        btnPopupOK.setText(button);

        layoutResultPopup.startAnimation(boardScale);
        layoutResultPopup.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePopup() {
        layoutResultPopup.setVisibility(View.GONE);
    }

    @Override
    public void showBlockQuiz() {
        layoutDailyLimitReached.startAnimation(boardScale);
        layoutDailyLimitReached.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBlockQuiz() {
        layoutDailyLimitReached.setVisibility(View.GONE);
    }

    @Override
    public void goBack() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new HomeFragment(),
                        Utils.UHomeFragment).commit();
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInterstitial() {

        if (AdmobApplication.isAdLoaded()){

            AdmobApplication.displayLoadedAd();

            AdmobApplication.mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    AdmobApplication.requestNewInterstitial();

                    Log.i("GLOBALAD", "onAdClosed");

                    presenter.onAdClosed();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();

                    presenter.onAdLeftApplication();
                    Log.i("GLOBALAD", "onAdLeftApplication");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.i("GLOBALAD", "onAdOpened");

                    presenter.onAdOpened();
                }

            });

        }else{
            AdmobApplication.requestNewInterstitial();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }
}