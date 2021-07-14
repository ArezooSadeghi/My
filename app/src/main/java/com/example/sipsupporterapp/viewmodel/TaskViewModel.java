package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.CustomerResult;
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
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<CaseResult.CaseInfo> caseFinishClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> refreshCaseFinishClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> changeCaseTypeClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CaseResult.CaseInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> refresh = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> registerCommentClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> assignToOthersClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CaseResult.CaseInfo> printInvoiceClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> caseProductsClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerResult> customerInfoResultSingleLiveEvent;
    private SingleLiveEvent<Boolean> navigateToCustomerFragment = new SingleLiveEvent<>();

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        caseTypesResultSingleLiveEvent = repository.getCaseTypesResultSingleLiveEvent();
        casesByCaseTypeResultSingleLiveEvent = repository.getCasesByCaseTypeResultSingleLiveEvent();
        addCaseResultSingleLiveEvent = repository.getAddCaseResultSingleLiveEvent();
        deleteCaseResultSingleLiveEvent = repository.getDeleteCaseResultSingleLiveEvent();
        editCaseResultSingleLiveEvent = repository.getEditCaseResultSingleLiveEvent();
        closeCaseResultSingleLiveEvent = repository.getCloseCaseResultSingleLiveEvent();
        customerInfoResultSingleLiveEvent = repository.getCustomerInfoResultSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
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

    public SingleLiveEvent<CaseResult> getDeleteCaseResultSingleLiveEvent() {
        return deleteCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getEditCaseResultSingleLiveEvent() {
        return editCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getCloseCaseResultSingleLiveEvent() {
        return closeCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult.CaseInfo> getCaseFinishClicked() {
        return caseFinishClicked;
    }

    public SingleLiveEvent<Boolean> getRefreshCaseFinishClicked() {
        return refreshCaseFinishClicked;
    }

    public SingleLiveEvent<Boolean> getChangeCaseTypeClicked() {
        return changeCaseTypeClicked;
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<CaseResult.CaseInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Boolean> getRefresh() {
        return refresh;
    }

    public SingleLiveEvent<Integer> getRegisterCommentClicked() {
        return registerCommentClicked;
    }

    public SingleLiveEvent<Integer> getAssignToOthersClicked() {
        return assignToOthersClicked;
    }

    public SingleLiveEvent<CaseResult.CaseInfo> getPrintInvoiceClicked() {
        return printInvoiceClicked;
    }

    public SingleLiveEvent<Integer> getCaseProductsClicked() {
        return caseProductsClicked;
    }

    public SingleLiveEvent<CustomerResult> getCustomerInfoResultSingleLiveEvent() {
        return customerInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getNavigateToCustomerFragment() {
        return navigateToCustomerFragment;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCaseTypeResult(String baseUrl) {
        repository.getSipSupporterServiceCaseTypeResult(baseUrl);
    }

    public void getSipSupporterServiceCasesByCaseType(String baseUrl) {
        repository.getSipSupporterServiceCaseResult(baseUrl);
    }

    public void getSipSupporterServiceCaseResult(String baseUrl) {
        repository.getSipSupporterServiceCaseResult(baseUrl);
    }

    public void getSupporterServiceCustomerResult(String baseUrl) {
        repository.getSupporterServiceCustomerResult(baseUrl);
    }

    public void fetchCaseTypes(String path, String userLoginKey) {
        repository.fetchCaseTypes(path, userLoginKey);
    }

    public void fetchCasesByCaseType(String path, String userLoginKey, int caseTypeID, String search, boolean showAll) {
        repository.fetchCasesByCaseType(path, userLoginKey, caseTypeID, search, showAll);
    }

    public void addCase(String path, String userLoginKey, CaseResult.CaseInfo caseInfo) {
        repository.addCase(path, userLoginKey, caseInfo);
    }

    public void deleteCase(String path, String userLoginKey, int caseID) {
        repository.deleteCase(path, userLoginKey, caseID);
    }

    public void editCase(String path, String userLoginKey, CaseResult.CaseInfo caseInfo) {
        repository.editCase(path, userLoginKey, caseInfo);
    }

    public void closeCase(String path, String userLoginKey, CaseResult.CaseInfo caseInfo) {
        repository.closeCase(path, userLoginKey, caseInfo);
    }

    public void fetchCustomerInfo(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerInfo(path, userLoginKey, customerID);
    }
}
