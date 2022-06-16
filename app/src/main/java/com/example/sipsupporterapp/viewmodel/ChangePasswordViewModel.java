package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class ChangePasswordViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<UserResult> changedPasswordResultSingleLiveEvent;
    private SingleLiveEvent<String> error;

    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        changedPasswordResultSingleLiveEvent = repository.getChangePasswordResultSingleLiveEvent();
        error = repository.getError();
    }

    public SingleLiveEvent<UserResult> getChangedPasswordResultSingleLiveEvent() {
        return changedPasswordResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getError() {
        return error;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerUserResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerUserResult(baseUrl);
    }

    public void changePassword(String path, String userLoginKey, String newPassword) {
        repository.changePassword(path, userLoginKey, newPassword);
    }
}
