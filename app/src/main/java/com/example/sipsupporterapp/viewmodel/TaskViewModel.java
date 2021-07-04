package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CaseInfo;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class TaskViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CaseTypeResult> caseTypesResultSingleLiveEvent;

    private SingleLiveEvent<CaseResult> casesByCaseTypeResultSingleLiveEvent;

    private SingleLiveEvent<CaseResult> addCaseResultSingleLiveEvent;

    private SingleLiveEvent<Boolean> caseFinishClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> changeCaseTypeClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> deleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> editClicked = new SingleLiveEvent<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        caseTypesResultSingleLiveEvent = repository.getCaseTypesResultSingleLiveEvent();

        casesByCaseTypeResultSingleLiveEvent = repository.getCasesByCaseTypeResultSingleLiveEvent();

        addCaseResultSingleLiveEvent = repository.getAddCaseResultSingleLiveEvent();
    }

    public SingleLiveEvent<CaseTypeResult> getCaseTypesResultSingleLiveEvent() {
        return caseTypesResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getCasesByCaseTypeResultSingleLiveEvent() {
        return casesByCaseTypeResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getAddCaseResultSingleLiveEvent() {
        return addCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getCaseFinishClicked() {
        return caseFinishClicked;
    }

    public SingleLiveEvent<Boolean> getChangeCaseTypeClicked() {
        return changeCaseTypeClicked;
    }

    public SingleLiveEvent<Boolean> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<Boolean> getEditClicked() {
        return editClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCaseTypes(String baseUrl) {
        repository.getSipSupporterServiceCaseTypes(baseUrl);
    }

    public void getSipSupporterServiceCasesByCaseType(String baseUrl) {
        repository.getSipSupporterServiceCasesByCaseType(baseUrl);
    }

    public void getSipSupporterServiceAddCase(String baseUrl) {
        repository.getSipSupporterServiceAddCase(baseUrl);
    }

    public void fetchCaseTypes(String path, String userLoginKey) {
        repository.fetchCaseTypes(path, userLoginKey);
    }

    public void fetchCasesByCaseType(String path, String userLoginKey, int caseTypeID, String search, boolean showAll) {
        repository.fetchCasesByCaseType(path, userLoginKey, caseTypeID, search, showAll);
    }

    public void addCase(String path, String userLoginKey, CaseInfo caseInfo) {
        repository.addCase(path, userLoginKey, caseInfo);
    }
}
