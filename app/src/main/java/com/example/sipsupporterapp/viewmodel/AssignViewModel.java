package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AssignViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<Boolean> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> registerCommentClicked = new SingleLiveEvent<>();

    public AssignViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
    }

    public SingleLiveEvent<Boolean> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Boolean> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<Boolean> getRegisterCommentClicked() {
        return registerCommentClicked;
    }
}
