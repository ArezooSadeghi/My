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
    private SingleLiveEvent<String> error;
    private SingleLiveEvent<CaseProductResult> addCaseProductResultSingleLiveEvent;
    private SingleLiveEvent<CaseProductResult> deleteCaseProductResultSingleLiveEvent;
    private SingleLiveEvent<CaseProductResult.CaseProductInfo> update = new SingleLiveEvent<>();
    private SingleLiveEvent<CaseProductResult.CaseProductInfo> itemClicked = new SingleLiveEvent<>();

    public CaseProductViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        caseProductsWithSelectedResultSingleLiveEvent = repository.getCaseProductsWithSelectedResultSingleLiveEvent();
        error = repository.getError();
        addCaseProductResultSingleLiveEvent = repository.getAddCaseProductResultSingleLiveEvent();
        deleteCaseProductResultSingleLiveEvent = repository.getDeleteCaseProductResultSingleLiveEvent();
    }

    public SingleLiveEvent<CaseProductResult> getCaseProductsWithSelectedResultSingleLiveEvent() {
        return caseProductsWithSelectedResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getError() {
        return error;
    }

    public SingleLiveEvent<CaseProductResult> getAddCaseProductResultSingleLiveEvent() {
        return addCaseProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseProductResult> getDeleteCaseProductResultSingleLiveEvent() {
        return deleteCaseProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseProductResult.CaseProductInfo> getUpdate() {
        return update;
    }

    public SingleLiveEvent<CaseProductResult.CaseProductInfo> getItemClicked() {
        return itemClicked;
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

    public CaseProductResult.CaseProductInfo getCaseProductInfoAt(Integer index) {
        if (caseProductsWithSelectedResultSingleLiveEvent.getValue() != null && index != null) {
            return caseProductsWithSelectedResultSingleLiveEvent.getValue().getCaseProducts()[index];
        }
        return null;
    }
}
