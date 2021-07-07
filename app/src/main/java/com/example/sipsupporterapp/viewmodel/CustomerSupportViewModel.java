package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerSupportViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CustomerSupportResult> customerSupportsResultSingleLiveEvent;

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    private SingleLiveEvent<CustomerSupportResult.CustomerSupportInfo> seeCustomerSupportAttachmentsClicked = new SingleLiveEvent<>();

    public CustomerSupportViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        customerSupportsResultSingleLiveEvent = repository.getCustomerSupportsResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportsResultSingleLiveEvent() {
        return customerSupportsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionSingleLiveEvent() {
        return noConnectionExceptionSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult.CustomerSupportInfo> getSeeCustomerSupportAttachmentsClicked() {
        return seeCustomerSupportAttachmentsClicked;
    }

    public void getSipSupporterServiceCustomerSupportResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerSupportResult(baseUrl);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void fetchCustomerSupports(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerSupports(path, userLoginKey, customerID);
    }
}
