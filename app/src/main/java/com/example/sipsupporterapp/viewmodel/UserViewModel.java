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
    private SingleLiveEvent<String> error;
    private SingleLiveEvent<CustomerUserResult.CustomerUserInfo> itemClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> refresh = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerUserResult.CustomerUserInfo> selected = new SingleLiveEvent<>();

    public UserViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        usersResultSingleLiveEvent = repository.getCustomerUsersResultSingleLiveEvent();
        dateResultSingleLiveEvent = repository.getDateResultSingleLiveEvent();
        error = repository.getError();
    }

    public SingleLiveEvent<CustomerUserResult> getUsersResultSingleLiveEvent() {
        return usersResultSingleLiveEvent;
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getError() {
        return error;
    }

    public SingleLiveEvent<Boolean> getRefresh() {
        return refresh;
    }

    public SingleLiveEvent<CustomerUserResult.CustomerUserInfo> getItemClicked() {
        return itemClicked;
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
        repository.fetchCustomerUsers(path, userLoginKey, customerID);
    }

    public void fetchDate(String path, String userLoginKey) {
        repository.fetchDate(path, userLoginKey);
    }
}
