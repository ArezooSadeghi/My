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
    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> searchQuery = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerResult.CustomerInfo> selected = new SingleLiveEvent<>();

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        customersResultSingleLiveEvent = repository.getCustomersResultSingleLiveEvent();
        dateResultSingleLiveEvent = repository.getDateResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerResult> getCustomersResultSingleLiveEvent() {
        return customersResultSingleLiveEvent;
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

    public SingleLiveEvent<String> getSearchQuery() {
        return searchQuery;
    }

    public SingleLiveEvent<CustomerResult.CustomerInfo> getSelected() {
        return selected;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSupporterServiceCustomerResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerResult(baseUrl);
    }

    public void getSipSupporterServiceDateResult(String baseUrl) {
        repository.getSipSupporterServiceDateResult(baseUrl);
    }

    public void fetchCustomers(String path, String userLoginKey, String customerName) {
        repository.fetchCustomers(path, userLoginKey, customerName);
    }

    public void fetchDate(String path, String userLoginKey) {
        repository.fetchDate(path, userLoginKey);
    }

    public void onItemClick(Integer index) {
        CustomerResult.CustomerInfo customerInfo = getCustomerInfoAt(index);
        selected.setValue(customerInfo);
    }

    public CustomerResult.CustomerInfo getCustomerInfoAt(Integer index) {
        if (customersResultSingleLiveEvent.getValue() != null && index != null) {
            return customersResultSingleLiveEvent.getValue().getCustomers()[index];
        }
        return null;
    }
}
