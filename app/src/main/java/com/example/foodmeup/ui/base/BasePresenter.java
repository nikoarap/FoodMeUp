package com.example.foodmeup.ui.base;

public abstract class BasePresenter<View extends BaseView> {

    protected View view;

    protected BasePresenter(View view) {
        this.view = view;
    }

    void destroyView() {
        //avoid memory leak
        view = null;
    }
}