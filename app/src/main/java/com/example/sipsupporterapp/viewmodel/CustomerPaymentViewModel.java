package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerPaymentViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerPaymentsResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsByBankAccountResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerPaymentsByBankAccountResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorAddCustomerPaymentResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorEditCustomerPaymentResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteCustomerPaymentResultSingleLiveEvent;

    private SingleLiveEvent<Boolean> updateListAddCustomerPaymentSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentInfo> DeleteCustomerPaymentClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentInfo> editCustomerPaymentClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentInfo> seeCustomerPaymentAttachmentsClicked = new SingleLiveEvent<>();


    public CustomerPaymentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        errorBankAccountsResultSingleLiveEvent = repository.getErrorBankAccountsResultSingleLiveEvent();

        customerPaymentsResultSingleLiveEvent = repository.getCustomerPaymentsResultSingleLiveEvent();
        errorCustomerPaymentsResultSingleLiveEvent = repository.getErrorCustomerPaymentsResultSingleLiveEvent();

        customerPaymentsByBankAccountResultSingleLiveEvent = repository.getCustomerPaymentsByBankAccountResultSingleLiveEvent();
        errorCustomerPaymentsByBankAccountResultSingleLiveEvent = repository.getErrorCustomerPaymentsByBankAccountResultSingleLiveEvent();

        addCustomerPaymentResultSingleLiveEvent = repository.getAddCustomerPaymentResultSingleLiveEvent();
        errorAddCustomerPaymentResultSingleLiveEvent = repository.getErrorAddCustomerPaymentResultSingleLiveEvent();

        editCustomerPaymentResultSingleLiveEvent = repository.getEditCustomerPaymentResultSingleLiveEvent();
        errorEditCustomerPaymentResultSingleLiveEvent = repository.getErrorEditCustomerPaymentResultSingleLiveEvent();

        deleteCustomerPaymentResultSingleLiveEvent = repository.getDeleteCustomerPaymentResultSingleLiveEvent();
        errorDeleteCustomerPaymentResultSingleLiveEvent = repository.getErrorDeleteCustomerPaymentResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsResultSingleLiveEvent() {
        return customerPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentsResultSingleLiveEvent() {
        return errorCustomerPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return customerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return errorCustomerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentResultSingleLiveEvent() {
        return addCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerPaymentResultSingleLiveEvent() {
        return errorAddCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentResultSingleLiveEvent() {
        return editCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerPaymentResultSingleLiveEvent() {
        return errorEditCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentResultSingleLiveEvent() {
        return deleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerPaymentResultSingleLiveEvent() {
        return errorDeleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getUpdateListAddCustomerPaymentSingleLiveEvent() {
        return updateListAddCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getDeleteCustomerPaymentClicked() {
        return DeleteCustomerPaymentClicked;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getEditCustomerPaymentClicked() {
        return editCustomerPaymentClicked;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getSeeCustomerPaymentAttachmentsClicked() {
        return seeCustomerPaymentAttachmentsClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerPaymentsResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerPaymentsResult(baseUrl);
    }

    public void getSipSupporterServiceCustomerPaymentsByBankAccount(String baseUrl) {
        repository.getSipSupporterServiceCustomerPaymentsByBankAccount(baseUrl);
    }

    public void getSipSupporterServiceBankAccounts(String baseUrl) {
        repository.getSipSupporterServiceBankAccounts(baseUrl);
    }

    public void getSipSupporterServiceAddCustomerPayment(String baseUrl) {
        repository.getSipSupporterServiceAddCustomerPayment(baseUrl);
    }

    public void getSipSupportServiceEditCustomerPayment(String baseUrl) {
        repository.getSipSupporterServiceEditCustomerPayment(baseUrl);
    }

    public void getSipSupporterServiceDeleteCustomerPayment(String baseUrl) {
        repository.getSipSupporterServiceDeleteCustomerPayment(baseUrl);
    }

    public void fetchCustomerPaymentsResult(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerPaymentsResult(path, userLoginKey, customerID);
    }

    public void fetchCustomerPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        repository.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }

    public void addCustomerPaymentResult(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        repository.addCustomerPayment(path, userLoginKey, customerPaymentInfo);
    }

    public void editCustomerPaymentResult(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        repository.editCustomerPayment(path, userLoginKey, customerPaymentInfo);
    }

    public void deleteCustomerPayment(String path, String userLoginKey, int customerPaymentID) {
        repository.deleteCustomerPayment(path, userLoginKey, customerPaymentID);
    }
}
