package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CommentViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<Boolean> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> editClicked = new SingleLiveEvent<>();

    public CommentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
    }

    public SingleLiveEvent<Boolean> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<Boolean> getEditClicked() {
        return editClicked;
    }
}
