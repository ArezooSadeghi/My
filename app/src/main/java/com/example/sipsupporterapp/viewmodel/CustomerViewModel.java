package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

import java.util.List;

public class CustomerViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<CustomerResult> customerResultSingleLiveEvent;
    private LiveData<String> wrongUserLoginKeyLiveData;
    private LiveData<String> notValueUserLoginKeyLiveData;
    private SingleLiveEvent<Boolean> showProgressBarSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> itemClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> noConnectivityExceptionSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;
    private SingleLiveEvent<String> errorSingleLiveEvent;
    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent;


    public CustomerViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        customerResultSingleLiveEvent = repository.getCustomerResultSingleLiveEvent();
        wrongUserLoginKeyLiveData = repository.getWrongUserLoginKeyMutableLiveData();
        notValueUserLoginKeyLiveData = repository.getNotValueUserLoginKeyMutableLiveData();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectivityExceptionSingleLiveEvent = repository.getNoConnectivityExceptionSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
        errorSingleLiveEvent = repository.getErrorSingleLiveEvent();
        dateResultSingleLiveEvent = repository.getDateResultSingleLiveEvent();
        noConnection = repository.getNoConnection();
    }

    public SingleLiveEvent<CustomerResult> getCustomerResultSingleLiveEvent() {
        return customerResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getShowProgressBarSingleLiveEvent() {
        return showProgressBarSingleLiveEvent;
    }

    public void fetchCustomerResult(String path, String userLoginKey, String customerName) {
        repository.fetchCustomerResult(path, userLoginKey, customerName);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public LiveData<String> getWrongUserLoginKeyLiveData() {
        return wrongUserLoginKeyLiveData;
    }

    public LiveData<String> getNotValueUserLoginKeyLiveData() {
        return notValueUserLoginKeyLiveData;
    }

    public SingleLiveEvent<Integer> getItemClickedSingleLiveEvent() {
        return itemClickedSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getNoConnectivityExceptionSingleLiveEvent() {
        return noConnectivityExceptionSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public void getSupportServicePostCustomerParameter(String baseUrl) {
        repository.getSupportServicePostCustomerParameter(baseUrl);
    }

    public SingleLiveEvent<String> getErrorSingleLiveEvent() {
        return errorSingleLiveEvent;
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public void getSipSupportServiceGetDateResult(String baseUrl) {
        repository.getSipSupportServiceGetDateResult(baseUrl);
    }

    public void fetchDateResult(String path, String userLoginKey) {
        repository.fetchDateResult(path, userLoginKey);
    }

    public void deleteServerData(ServerData serverData) {
        repository.deleteServerData(serverData);
    }

    public List<ServerData> getServerDataList() {
        return repository.getServerDataList();
    }
}
