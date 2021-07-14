package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AddEditCustomerSupportViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<SupportEventResult> supportEventsResultSingleLiveEvent;
    private SingleLiveEvent<CustomerSupportResult> addCustomerSupportResultSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    public AddEditCustomerSupportViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        supportEventsResultSingleLiveEvent = repository.getSupportEventsResultSingleLiveEvent();
        addCustomerSupportResultSingleLiveEvent = repository.getAddCustomerSupportResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventsResultSingleLiveEvent() {
        return supportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getAddCustomerSupportResultSingleLiveEvent() {
        return addCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceSupportEventResult(String baseUrl) {
        repository.getSipSupporterServiceSupportEventResult(baseUrl);
    }

    public void getSipSupporterServiceCustomerSupportResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerSupportResult(baseUrl);
    }

    public void fetchSupportEventsResult(String path, String userLoginKey) {
        repository.fetchSupportEvents(path, userLoginKey);
    }

    public void addCustomerSupport(String path, String userLoginKey, CustomerSupportResult.CustomerSupportInfo customerSupportInfo) {
        repository.addCustomerSupport(path, userLoginKey, customerSupportInfo);
    }
}
