package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class UserViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CustomerUserResult> usersResultSingleLiveEvent;

    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent;

    private SingleLiveEvent<Integer> itemClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<Boolean> successfulRegisterCustomerUsersSingleLiveEvent = new SingleLiveEvent<>();

    public UserViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        usersResultSingleLiveEvent = repository.getCustomerUsersResultSingleLiveEvent();

        dateResultSingleLiveEvent = repository.getDateResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerUserResult> getUsersResultSingleLiveEvent() {
        return usersResultSingleLiveEvent;
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public SingleLiveEvent<Integer> getItemClicked() {
        return itemClicked;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getSuccessfulRegisterCustomerUsersSingleLiveEvent() {
        return successfulRegisterCustomerUsersSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerUserResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerUserResult(baseUrl);
    }

    public void getSipSupporterServiceDateResult(String baseUrl) {
        repository.getSipSupporterServiceDateResult(baseUrl);
    }

    public void fetchUsers(String path, String userLoginKey, int customerID) {
        repository.fetchUsers(path, userLoginKey, customerID);
    }

    public void fetchDate(String path, String userLoginKey) {
        repository.fetchDate(path, userLoginKey);
    }
}
