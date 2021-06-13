package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class NewCustomerPaymentsViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsByBankAccountResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerPaymentsByBankAccountResultSingleLiveEvent;

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent;

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentInfo> editCustomerPaymentClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentInfo> deleteCustomerPaymentClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentInfo> seeCustomerPaymentAttachmentsClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> okDeleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteCustomerPaymentResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentsSingleLiveEvent;
    private SingleLiveEvent<String> errorEditCustomerPaymentsSingleLiveEvent;

    private SingleLiveEvent<Boolean> dialogDismissed = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentsSingleLiveEvent;
    private SingleLiveEvent<String> errorAddCustomerPaymentsSingleLiveEvent;

    public NewCustomerPaymentsViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        customerPaymentsByBankAccountResultSingleLiveEvent = repository.getCustomerPaymentsByBankAccountResultSingleLiveEvent();
        errorCustomerPaymentsByBankAccountResultSingleLiveEvent = repository.getErrorCustomerPaymentsByBankAccountResultSingleLiveEvent();

        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        errorBankAccountsResultSingleLiveEvent = repository.getErrorBankAccountsResultSingleLiveEvent();

        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        deleteCustomerPaymentResultSingleLiveEvent = repository.getDeleteCustomerPaymentResultSingleLiveEvent();
        errorDeleteCustomerPaymentResultSingleLiveEvent = repository.getErrorDeleteCustomerPaymentResultSingleLiveEvent();

        editCustomerPaymentsSingleLiveEvent = repository.getEditCustomerPaymentResultSingleLiveEvent();
        errorEditCustomerPaymentsSingleLiveEvent = repository.getErrorEditCustomerPaymentResultSingleLiveEvent();

        addCustomerPaymentsSingleLiveEvent = repository.getAddCustomerPaymentResultSingleLiveEvent();
        errorAddCustomerPaymentsSingleLiveEvent = repository.getErrorAddCustomerPaymentResultSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return customerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return errorCustomerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getEditCustomerPaymentClicked() {
        return editCustomerPaymentClicked;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getDeleteCustomerPaymentClicked() {
        return deleteCustomerPaymentClicked;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getSeeCustomerPaymentAttachmentsClicked() {
        return seeCustomerPaymentAttachmentsClicked;
    }

    public SingleLiveEvent<Boolean> getOkDeleteClicked() {
        return okDeleteClicked;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentResultSingleLiveEvent() {
        return deleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerPaymentResultSingleLiveEvent() {
        return errorDeleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentsSingleLiveEvent() {
        return editCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerPaymentsSingleLiveEvent() {
        return errorEditCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDialogDismissed() {
        return dialogDismissed;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentsSingleLiveEvent() {
        return addCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerPaymentsSingleLiveEvent() {
        return errorAddCustomerPaymentsSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerPaymentsByBankAccount(String baseUrl) {
        repository.getSipSupporterServiceCustomerPaymentsByBankAccount(baseUrl);
    }

    public void getSipSupporterServiceBankAccounts(String baseUrl) {
        repository.getSipSupporterServiceBankAccounts(baseUrl);
    }

    public void getSipSupportServiceDeleteCustomerPayments(String baseUrl) {
        repository.getSipSupportServiceDeleteCustomerPayments(baseUrl);
    }

    public void fetchCustomerPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        repository.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }

    public void deleteCustomerPayments(String path, String userLoginKey, int customerPaymentID) {
        repository.deleteCustomerPayments(path, userLoginKey, customerPaymentID);
    }
}
