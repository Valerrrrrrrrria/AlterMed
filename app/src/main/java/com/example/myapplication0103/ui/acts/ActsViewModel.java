package com.example.myapplication0103.ui.acts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ActsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is acts Of Implantation fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}