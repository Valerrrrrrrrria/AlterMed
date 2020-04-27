package com.example.myapplication0103.ui.reg;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RegViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Reg fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}