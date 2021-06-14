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

    private SingleLiveEvent<SupportEventResult> supportEventsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorSupportEventsResultSingleLiveEvent;

    private SingleLiveEvent<CustomerSupportResult> addCustomerSupportResultSingleLiveEvent;
    private SingleLiveEvent<String> errorAddCustomerSupportResultSingleLiveEvent;

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    public AddEditCustomerSupportViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        supportEventsResultSingleLiveEvent = repository.getSupportEventsResultSingleLiveEvent();
        errorSupportEventsResultSingleLiveEvent = repository.getErrorSupportEventsResultSingleLiveEvent();

        addCustomerSupportResultSingleLiveEvent = repository.getAddCustomerSupportResultSingleLiveEvent();
        errorAddCustomerSupportResultSingleLiveEvent = repository.getErrorAddCustomerSupportResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventsResultSingleLiveEvent() {
        return supportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorSupportEventsResultSingleLiveEvent() {
        return errorSupportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getAddCustomerSupportResultSingleLiveEvent() {
        return addCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerSupportResultSingleLiveEvent() {
        return errorAddCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceSupportEventsResult(String baseUrl) {
        repository.getSipSupporterServiceSupportEventsResult(baseUrl);
    }

    public void getSipSupporterServiceAddCustomerSupportResult(String baseUrl) {
        repository.getSipSupporterServiceAddCustomerSupportResult(baseUrl);
    }

    public void fetchSupportEventsResult(String path, String userLoginKey) {
        repository.fetchSupportEventsResult(path, userLoginKey);
    }

    public void addCustomerSupport(String path, String userLoginKey, CustomerSupportInfo customerSupportInfo) {
        repository.addCustomerSupport(path, userLoginKey, customerSupportInfo);
    }
}
