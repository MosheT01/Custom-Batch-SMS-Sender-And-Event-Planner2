package com.example.custombatchsmssenderandeventplanner.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> messageText;

    public HomeViewModel() {
        messageText = new MutableLiveData<>();
        messageText.setValue("This is home fragment1");
    }

    public LiveData<String> getText() {
        return messageText;
    }
}