package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CustomerResult> customersResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomersResultSingleLiveEvent;

    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent;

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    private SingleLiveEvent<Boolean> showProgressBarSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> itemClicked = new SingleLiveEvent<>();


    public CustomerViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        customersResultSingleLiveEvent = repository.getCustomersResultSingleLiveEvent();
        errorCustomersResultSingleLiveEvent = repository.getErrorCustomersResultSingleLiveEvent();

        dateResultSingleLiveEvent = repository.getDateResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerResult> getCustomersResultSingleLiveEvent() {
        return customersResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomersResultSingleLiveEvent() {
        return errorCustomersResultSingleLiveEvent;
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
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

    public SingleLiveEvent<Boolean> getShowProgressBarSingleLiveEvent() {
        return showProgressBarSingleLiveEvent;
    }

    public SingleLiveEvent<Integer> getItemClicked() {
        return itemClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSupporterServicePostCustomerParameter(String baseUrl) {
        repository.getSupporterServicePostCustomerParameter(baseUrl);
    }

    public void getSipSupporterServiceDateResult(String baseUrl) {
        repository.getSipSupporterServiceDateResult(baseUrl);
    }

    public void fetchCustomersResult(String path, String userLoginKey, String customerName) {
        repository.fetchCustomersResult(path, userLoginKey, customerName);
    }

    public void fetchDateResult(String path, String userLoginKey) {
        repository.fetchDate(path, userLoginKey);
    }
}
