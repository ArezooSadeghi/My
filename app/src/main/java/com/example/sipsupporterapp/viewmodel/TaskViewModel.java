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

    private SingleLiveEvent<CaseResult> deleteCaseResultSingleLiveEvent;

    private SingleLiveEvent<CaseResult> editCaseResultSingleLiveEvent;

    private SingleLiveEvent<CaseResult> closeCaseResultSingleLiveEvent;

    private SingleLiveEvent<CaseInfo> caseFinishClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> refreshCaseFinishClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> changeCaseTypeClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> refresh = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> registerCommentClicked = new SingleLiveEvent<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        caseTypesResultSingleLiveEvent = repository.getCaseTypesResultSingleLiveEvent();

        casesByCaseTypeResultSingleLiveEvent = repository.getCasesByCaseTypeResultSingleLiveEvent();

        addCaseResultSingleLiveEvent = repository.getAddCaseResultSingleLiveEvent();

        deleteCaseResultSingleLiveEvent = repository.getDeleteCaseResultSingleLiveEvent();

        editCaseResultSingleLiveEvent = repository.getEditCaseResultSingleLiveEvent();

        closeCaseResultSingleLiveEvent = repository.getCloseCaseResultSingleLiveEvent();
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

    public SingleLiveEvent<CaseInfo> getCaseFinishClicked() {
        return caseFinishClicked;
    }

    public SingleLiveEvent<Boolean> getChangeCaseTypeClicked() {
        return changeCaseTypeClicked;
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<CaseInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Boolean> getRegisterCommentClicked() {
        return registerCommentClicked;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public SingleLiveEvent<CaseResult> getDeleteCaseResultSingleLiveEvent() {
        return deleteCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getEditCaseResultSingleLiveEvent() {
        return editCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRefresh() {
        return refresh;
    }

    public SingleLiveEvent<CaseResult> getCloseCaseResultSingleLiveEvent() {
        return closeCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRefreshCaseFinishClicked() {
        return refreshCaseFinishClicked;
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

    public void getSipSupporterServiceDeleteCase(String baseUrl) {
        repository.getSipSupporterServiceDeleteCase(baseUrl);
    }

    public void getSipSupporterServiceEditCase(String baseUrl) {
        repository.getSipSupporterServiceEditCase(baseUrl);
    }

    public void getSipSupporterServiceCloseCase(String baseUrl) {
        repository.getSipSupporterServiceCloseCase(baseUrl);
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

    public void deleteCase(String path, String userLoginKey, int caseID) {
        repository.deleteCase(path, userLoginKey, caseID);
    }

    public void editCase(String path, String userLoginKey, CaseInfo caseInfo) {
        repository.editCase(path, userLoginKey, caseInfo);
    }

    public void closeCase(String path, String userLoginKey, CaseInfo caseInfo) {
        repository.closeCase(path, userLoginKey, caseInfo);
    }
}
