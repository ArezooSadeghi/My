package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerSupportViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<CustomerSupportResult> customerSupportsResultSingleLiveEvent;
    private SingleLiveEvent<CustomerSupportResult> addCustomerSupportResultSingleLiveEvent;
    private SingleLiveEvent<SupportEventResult> supportEventsResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<CustomerSupportResult.CustomerSupportInfo> seeCustomerSupportAttachmentsClicked = new SingleLiveEvent<>();

    public CustomerSupportViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        customerSupportsResultSingleLiveEvent = repository.getCustomerSupportsResultSingleLiveEvent();
        addCustomerSupportResultSingleLiveEvent = repository.getAddCustomerSupportResultSingleLiveEvent();
        supportEventsResultSingleLiveEvent = repository.getSupportEventsResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportsResultSingleLiveEvent() {
        return customerSupportsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getAddCustomerSupportResultSingleLiveEvent() {
        return addCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventsResultSingleLiveEvent() {
        return supportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult.CustomerSupportInfo> getSeeAttachmentsClicked() {
        return seeCustomerSupportAttachmentsClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerSupportResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerSupportResult(baseUrl);
    }

    public void getSipSupporterServiceSupportEventResult(String baseUrl) {
        repository.getSipSupporterServiceSupportEventResult(baseUrl);
    }

    public void fetchCustomerSupports(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerSupports(path, userLoginKey, customerID);
    }

    public void fetchSupportEventsResult(String path, String userLoginKey) {
        repository.fetchSupportEvents(path, userLoginKey);
    }

    public void addCustomerSupport(String path, String userLoginKey, CustomerSupportResult.CustomerSupportInfo customerSupportInfo) {
        repository.addCustomerSupport(path, userLoginKey, customerSupportInfo);
    }

    public CustomerSupportResult.CustomerSupportInfo getCustomerSupportInfoAt(Integer index) {
        if (customerSupportsResultSingleLiveEvent.getValue() != null && index != null) {
            return customerSupportsResultSingleLiveEvent.getValue().getCustomerSupports()[index];
        }
        return null;
    }
}
