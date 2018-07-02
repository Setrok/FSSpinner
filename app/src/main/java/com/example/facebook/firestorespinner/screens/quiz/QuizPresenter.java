package com.example.facebook.firestorespinner.screens.quiz;

public class QuizPresenter implements IQuiz.Presenter {

    IQuiz.View view;

    QuizPresenter(IQuiz.View view){
        this.view = view;
    }

    @Override
    public void onCreate() {
        view.showPopup();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPopupOkClick() {
        view.hidePopup();
    }
}
