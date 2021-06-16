package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserLoginParameter;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent;
    private SingleLiveEvent<Boolean> insertSpinnerSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> insertIPAddressListSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<ServerData> deleteIPAddressListSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<ServerData> updateIPAddressListSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<UserResult> userResultSingleLiveEvent;
    private SingleLiveEvent<String> errorUserResult;

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;


    private SingleLiveEvent<ServerData> yesDeleteSpinner = new SingleLiveEvent<>();
    private SingleLiveEvent<ServerData> yesDeleteIPAddressList = new SingleLiveEvent<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        wrongIpAddressSingleLiveEvent = repository.getWrongIpAddressSingleLiveEvent();
        userResultSingleLiveEvent = repository.getUserLoginResultSingleLiveEvent();
        errorUserResult = repository.getErrorUserLoginResultSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<String> getWrongIpAddressSingleLiveEvent() {
        return wrongIpAddressSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getInsertSpinnerSingleLiveEvent() {
        return insertSpinnerSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getInsertIPAddressListSingleLiveEvent() {
        return insertIPAddressListSingleLiveEvent;
    }

    public SingleLiveEvent<ServerData> getDeleteIPAddressListSingleLiveEvent() {
        return deleteIPAddressListSingleLiveEvent;
    }

    public SingleLiveEvent<ServerData> getUpdateIPAddressListSingleLiveEvent() {
        return updateIPAddressListSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getUserResultSingleLiveEvent() {
        return userResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorUserResult() {
        return errorUserResult;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<ServerData> getYesDeleteSpinner() {
        return yesDeleteSpinner;
    }

    public SingleLiveEvent<ServerData> getYesDeleteIPAddressList() {
        return yesDeleteIPAddressList;
    }

    public void getSipSupportServicePostUserLoginParameter(String baseUrl) {
        repository.getSipSupportServicePostUserLoginParameter(baseUrl);
    }

    public void insertServerData(ServerData serverData) {
        repository.insertServerData(serverData);
    }

    public void fetchUserResult(String path, UserLoginParameter userLoginParameter) {
        repository.fetchUserResult(path, userLoginParameter);
    }

    public List<ServerData> getServerDataList() {
        return repository.getServerDataList();
    }

    public void deleteServerData(ServerData serverData) {
        repository.deleteServerData(serverData);
    }


    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }
}
