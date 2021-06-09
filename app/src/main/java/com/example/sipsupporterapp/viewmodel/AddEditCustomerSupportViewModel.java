package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerSupportInfo;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AddEditCustomerSupportViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;
    private SingleLiveEvent<SupportEventResult> supportEventResultSingleLiveEvent;
    private SingleLiveEvent<String> errorSupportEventResultSingleLiveEvent;
    private SingleLiveEvent<CustomerSupportResult> customerSupportResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerSupportResultSingleLiveEvent;
    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    public AddEditCustomerSupportViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        supportEventResultSingleLiveEvent = repository.getSupportEventsResultSingleLiveEvent();
        errorSupportEventResultSingleLiveEvent = repository.getErrorSupportEventsResultSingleLiveEvent();
        customerSupportResultSingleLiveEvent = repository.getAddCustomerSupportResultSingleLiveEvent();
        errorCustomerSupportResultSingleLiveEvent = repository.getErrorAddCustomerSupportResultSingleLiveEvent();
        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventsResultSingleLiveEvent() {
        return supportEventResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorSupportEventsResultSingleLiveEvent() {
        return errorSupportEventResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getAddCustomerSupportResultSingleLiveEvent() {
        return customerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerSupportResultSingleLiveEvent() {
        return errorCustomerSupportResultSingleLiveEvent;
    }

    public void getSipSupportServiceSupportEventResult(String baseUrl) {
        repository.getSipSupportServiceSupportEventResult(baseUrl);
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public void fetchSupportEventResult(String path, String userLoginKey) {
        repository.fetchSupportEventResult(path, userLoginKey);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void postCustomerSupportInfo(String path, String userLoginKey, CustomerSupportInfo customerSupportInfo) {
        repository.postCustomerSupportInfo(path, userLoginKey, customerSupportInfo);
    }

    public void getSipSupportServicePostCustomerSupportResult(String baseUrl) {
        repository.getSipSupportServicePostCustomerSupportResult(baseUrl);
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }
}
