package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

import java.util.List;

public class ChangePasswordViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;
    private SingleLiveEvent<UserResult> changedPassword;
    private SingleLiveEvent<String> errorChangedPassword;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnection;


    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        changedPassword = repository.getChangePasswordResultSingleLiveEvent();
        errorChangedPassword = repository.getErrorChangePasswordResultSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<UserResult> getChangedPassword() {
        return changedPassword;
    }

    public SingleLiveEvent<String> getErrorChangedPassword() {
        return errorChangedPassword;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public void changePassword(String path, String userLoginKey, String newPassword) {
        repository.changePassword(path, userLoginKey, newPassword);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupportServiceChangePassword(String baseUrl) {
        repository.getSipSupportServiceChangePassword(baseUrl);
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }
}
