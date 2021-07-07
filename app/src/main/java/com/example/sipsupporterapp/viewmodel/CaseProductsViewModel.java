package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CaseProductsViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CaseProductResult> caseProductsWithSelectedResultSingleLiveEvent;

    public CaseProductsViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        caseProductsWithSelectedResultSingleLiveEvent = repository.getCaseProductsWithSelectedResultSingleLiveEvent();
    }

    public SingleLiveEvent<CaseProductResult> getCaseProductsWithSelectedResultSingleLiveEvent() {
        return caseProductsWithSelectedResultSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCaseProductsWithSelected(String baseUrl) {
        repository.getSipSupporterServiceCaseProductsWithSelected(baseUrl);
    }

    public void fetchCaseProductsWithSelected(String path, String userLoginKey, int caseID) {
        repository.fetchCaseProductsWithSelected(path, userLoginKey, caseID);
    }
}
