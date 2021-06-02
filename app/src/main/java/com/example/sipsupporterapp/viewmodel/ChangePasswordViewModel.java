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
        changedPassword = repository.getChangedPassword();
        errorChangedPassword = repository.getErrorChangedPassword();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        noConnection = repository.getNoConnection();
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

    public void deleteServerData(ServerData serverData) {
        repository.deleteServerData(serverData);
    }

    public List<ServerData> getServerDataList() {
        return repository.getServerDataList();
    }
}
