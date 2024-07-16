package com.example.custombatchsmssenderandeventplanner.ui.Report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {
 
    private final MutableLiveData<String> reportText;

    public ReportViewModel() {
        reportText = new MutableLiveData<>();
        reportText.setValue("This is report fragment1");
    }

    public LiveData<String> getText() {
        return reportText;
    }
}
