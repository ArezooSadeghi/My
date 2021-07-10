package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CaseProductViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CaseProductResult> caseProductsWithSelectedResultSingleLiveEvent;

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<CaseProductResult> addCaseProductResultSingleLiveEvent;
    private SingleLiveEvent<CaseProductResult> deleteCaseProductResultSingleLiveEvent;

    private SingleLiveEvent<CaseProductResult.CaseProductInfo> update = new SingleLiveEvent<>();

    public CaseProductViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        caseProductsWithSelectedResultSingleLiveEvent = repository.getCaseProductsWithSelectedResultSingleLiveEvent();

        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        addCaseProductResultSingleLiveEvent = repository.getAddCaseProductResultSingleLiveEvent();
        deleteCaseProductResultSingleLiveEvent = repository.getDeleteCaseProductResultSingleLiveEvent();
    }

    public SingleLiveEvent<CaseProductResult> getCaseProductsWithSelectedResultSingleLiveEvent() {
        return caseProductsWithSelectedResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<CaseProductResult.CaseProductInfo> getUpdate() {
        return update;
    }

    public SingleLiveEvent<CaseProductResult> getAddCaseProductResultSingleLiveEvent() {
        return addCaseProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseProductResult> getDeleteCaseProductResultSingleLiveEvent() {
        return deleteCaseProductResultSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCaseProductResult(String baseUrl) {
        repository.getSipSupporterServiceCaseProductResult(baseUrl);
    }

    public void fetchCaseProductsWithSelected(String path, String userLoginKey, int caseID) {
        repository.fetchCaseProductsWithSelected(path, userLoginKey, caseID);
    }

    public void addCaseProduct(String path, String userLoginKey, CaseProductResult.CaseProductInfo caseProductInfo) {
        repository.addCaseProduct(path, userLoginKey, caseProductInfo);
    }

    public void deleteCaseProduct(String path, String userLoginKey, int caseProductID) {
        repository.deleteCaseProduct(path, userLoginKey, caseProductID);
    }
}
