package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerPaymentViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsResultSingleLiveEvent;
    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsByBankAccountResultSingleLiveEvent;
    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<CustomerPaymentResult> customerPaymentInfoResultSingleLiveEvent;
    private SingleLiveEvent<CustomerResult> customerInfoResultSingleLiveEvent;
    private SingleLiveEvent<CaseResult> caseInfoResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> updateListAddCustomerPaymentSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentResult.CustomerPaymentInfo> DeleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentResult.CustomerPaymentInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentResult.CustomerPaymentInfo> seeCustomerPaymentAttachmentsClicked = new SingleLiveEvent<>();

    public CustomerPaymentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        customerPaymentsResultSingleLiveEvent = repository.getCustomerPaymentsResultSingleLiveEvent();
        customerPaymentsByBankAccountResultSingleLiveEvent = repository.getCustomerPaymentsByBankAccountResultSingleLiveEvent();
        addCustomerPaymentResultSingleLiveEvent = repository.getAddCustomerPaymentResultSingleLiveEvent();
        editCustomerPaymentResultSingleLiveEvent = repository.getEditCustomerPaymentResultSingleLiveEvent();
        deleteCustomerPaymentResultSingleLiveEvent = repository.getDeleteCustomerPaymentResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        customerPaymentInfoResultSingleLiveEvent = repository.getCustomerPaymentInfoResultSingleLiveEvent();
        customerInfoResultSingleLiveEvent = repository.getCustomerInfoResultSingleLiveEvent();
        caseInfoResultSingleLiveEvent = repository.getCaseInfoResultSingleLiveEvent();
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsResultSingleLiveEvent() {
        return customerPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return customerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentResultSingleLiveEvent() {
        return addCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentResultSingleLiveEvent() {
        return editCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentResultSingleLiveEvent() {
        return deleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRefresh() {
        return updateListAddCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult.CustomerPaymentInfo> getDeleteClicked() {
        return DeleteClicked;
    }

    public SingleLiveEvent<CustomerPaymentResult.CustomerPaymentInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<CustomerPaymentResult.CustomerPaymentInfo> getSeeCustomerPaymentAttachmentsClicked() {
        return seeCustomerPaymentAttachmentsClicked;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentInfoResultSingleLiveEvent() {
        return customerPaymentInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerResult> getCustomerInfoResultSingleLiveEvent() {
        return customerInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getCaseInfoResultSingleLiveEvent() {
        return caseInfoResultSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerPaymentResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerPaymentResult(baseUrl);
    }

    public void getSipSupporterServiceCustomerResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerResult(baseUrl);
    }

    public void getSipSupporterServiceCaseResult(String baseUrl) {
        repository.getSipSupporterServiceCaseResult(baseUrl);
    }

    public void fetchCustomerPaymentsResult(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerPayments(path, userLoginKey, customerID);
    }

    public void fetchCustomerPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        repository.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }

    public void addCustomerPaymentResult(String path, String userLoginKey, CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        repository.addCustomerPayment(path, userLoginKey, customerPaymentInfo);
    }

    public void editCustomerPaymentResult(String path, String userLoginKey, CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        repository.editCustomerPayment(path, userLoginKey, customerPaymentInfo);
    }

    public void deleteCustomerPayment(String path, String userLoginKey, int customerPaymentID) {
        repository.deleteCustomerPayment(path, userLoginKey, customerPaymentID);
    }

    public void fetchCustomerPaymentInfo(String path, String userLoginKey, int customerPaymentID) {
        repository.fetchCustomerPaymentInfo(path, userLoginKey, customerPaymentID);
    }

    public void fetchCustomerInfo(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerInfo(path, userLoginKey, customerID);
    }

    public void fetchCaseInfo(String path, String userLoginKey, int caseID) {
        repository.fetchCaseInfo(path, userLoginKey, caseID);
    }
}
