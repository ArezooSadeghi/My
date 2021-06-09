package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.CustomerSupportInfo;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

import java.util.List;

public class CustomerSupportViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;
    private SingleLiveEvent<CustomerSupportResult> customerSupportResult;
    private SingleLiveEvent<String> errorCustomerSupportResult;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;
    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<CustomerSupportInfo> mCustomerSupportInfoAdapterSeeDocumentClickedSingleLiveEvent = new SingleLiveEvent<>();

    public CustomerSupportViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        customerSupportResult = repository.getCustomerSupportsResultSingleLiveEvent();
        errorCustomerSupportResult = repository.getErrorCustomerSupportsResultSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportsResultSingleLiveEvent() {
        return customerSupportResult;
    }

    public SingleLiveEvent<String> getErrorCustomerSupportsResultSingleLiveEvent() {
        return errorCustomerSupportResult;
    }

    public void getCustomerSupportResult(String path, String userLoginKey, int customerID) {
        repository.getCustomerSupportResult(path, userLoginKey, customerID);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupportServiceCustomerSupportResult(String baseUrl) {
        repository.getSipSupportServiceCustomerSupportResult(baseUrl);
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportInfo> getCustomerSupportInfoAdapterSeeDocumentClickedSingleLiveEvent() {
        return mCustomerSupportInfoAdapterSeeDocumentClickedSingleLiveEvent;
    }
}
