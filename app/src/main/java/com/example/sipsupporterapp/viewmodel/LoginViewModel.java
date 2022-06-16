package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<String> error;
    private SingleLiveEvent<Boolean> insertSpinnerSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> insertIPAddressListSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<ServerData> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<ServerData> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<UserResult> userResultSingleLiveEvent;
    private SingleLiveEvent<ServerData> yesDeleteIPAddressList = new SingleLiveEvent<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        error = repository.getError();
        userResultSingleLiveEvent = repository.getUserLoginResultSingleLiveEvent();
    }

    public SingleLiveEvent<String> getError() {
        return error;
    }

    public SingleLiveEvent<Boolean> getInsertSpinnerSingleLiveEvent() {
        return insertSpinnerSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getInsertIPAddressListSingleLiveEvent() {
        return insertIPAddressListSingleLiveEvent;
    }

    public SingleLiveEvent<ServerData> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<ServerData> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<UserResult> getUserResultSingleLiveEvent() {
        return userResultSingleLiveEvent;
    }

    public SingleLiveEvent<ServerData> getYesDeleteIPAddressList() {
        return yesDeleteIPAddressList;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceUserResult(String baseUrl) {
        repository.getSipSupporterServiceUserResult(baseUrl);
    }

    public void insertServerData(ServerData serverData) {
        repository.insertServerData(serverData);
    }

    public void fetchUserResult(String path, UserResult.UserLoginParameter userLoginParameter) {
        repository.login(path, userLoginParameter);
    }

    public List<ServerData> getServerDataList() {
        return repository.getServerDataList();
    }

    public void deleteServerData(ServerData serverData) {
        repository.deleteServerData(serverData);
    }
}
