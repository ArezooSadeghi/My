package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;
    private SingleLiveEvent<CustomerUserResult> customerUserResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerUserResultSingleLiveEvent;
    private SingleLiveEvent<Integer> itemClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;
    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent;
    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> successfulRegisterCustomerUsersSingleLiveEvent = new SingleLiveEvent<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        customerUserResultSingleLiveEvent = repository.getCustomerUsersResultSingleLiveEvent();
        errorCustomerUserResultSingleLiveEvent = repository.getErrorCustomerUsersResultSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        dateResultSingleLiveEvent = repository.getDateResultSingleLiveEvent();
        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerUserResult> getUsersResultSingleLiveEvent() {
        return customerUserResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorUsersResultSingleLiveEvent() {
        return errorCustomerUserResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<Integer> getItemClicked() {
        return itemClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupportServiceCustomerUserResult(String baseUrl) {
        repository.getSipSupportServiceCustomerUserResult(baseUrl);
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public void fetchCustomerUserResult(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerUserResult(path, userLoginKey, customerID);
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public void getSipSupportServiceGetDateResult(String baseUrl) {
        repository.getSipSupportServiceGetDateResult(baseUrl);
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<Boolean> getSuccessfulRegisterCustomerUsersSingleLiveEvent() {
        return successfulRegisterCustomerUsersSingleLiveEvent;
    }

    public void fetchDateResult(String path, String userLoginKey) {
        repository.fetchDateResult(path, userLoginKey);
    }
}
