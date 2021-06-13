package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerPaymentViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerPaymentResultSingleLiveEvent;

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnection;

    private SingleLiveEvent<CustomerPaymentInfo> seeDocumentsClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDeleteCustomerPaymentsSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentInfo> DeleteCustomerPaymentsClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerPaymentInfo> editCustomerPaymentsClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentsSingleLiveEvent;
    private SingleLiveEvent<String> errorAddCustomerPaymentSingleLiveEvent;

    private SingleLiveEvent<Boolean> updateListAddCustomerPaymentSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentsSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteCustomerPaymentSingleLiveEvent;

    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentsSingleLiveEvent;
    private SingleLiveEvent<String> errorEditCustomerPaymentSingleLiveEvent;

    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;


    public CustomerPaymentViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());

        customerPaymentResultSingleLiveEvent = repository.getCustomerPaymentsResultSingleLiveEvent();
        errorCustomerPaymentResultSingleLiveEvent = repository.getErrorCustomerPaymentsResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();

        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        errorBankAccountsResultSingleLiveEvent = repository.getErrorBankAccountsResultSingleLiveEvent();

        addCustomerPaymentsSingleLiveEvent = repository.getAddCustomerPaymentResultSingleLiveEvent();
        errorAddCustomerPaymentSingleLiveEvent = repository.getErrorAddCustomerPaymentResultSingleLiveEvent();

        deleteCustomerPaymentsSingleLiveEvent = repository.getDeleteCustomerPaymentResultSingleLiveEvent();
        errorDeleteCustomerPaymentSingleLiveEvent = repository.getErrorDeleteCustomerPaymentResultSingleLiveEvent();

        editCustomerPaymentsSingleLiveEvent = repository.getEditCustomerPaymentResultSingleLiveEvent();
        errorEditCustomerPaymentSingleLiveEvent = repository.getErrorEditCustomerPaymentResultSingleLiveEvent();

        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsResultSingleLiveEvent() {
        return customerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentsResultSingleLiveEvent() {
        return errorCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getSeeDocumentsClickedSingleLiveEvent() {
        return seeDocumentsClickedSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesDeleteCustomerPayment() {
        return yesDeleteCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public SingleLiveEvent<CustomerPaymentInfo> getDeleteCustomerPaymentClicked() {
        return DeleteCustomerPaymentsClickedSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentInfo> getEditCustomerPaymentClicked() {
        return editCustomerPaymentsClickedSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentResultSingleLiveEvent() {
        return addCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerPaymentResultSingleLiveEvent() {
        return errorAddCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getUpdateListAddCustomerPaymentSingleLiveEvent() {
        return updateListAddCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentResultSingleLiveEvent() {
        return deleteCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerPaymentResultSingleLiveEvent() {
        return errorDeleteCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentResultSingleLiveEvent() {
        return editCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerPaymentResultSingleLiveEvent() {
        return errorEditCustomerPaymentSingleLiveEvent;
    }

    public void getSipSupportServiceCustomerPaymentResult(String baseUrl) {
        repository.getSipSupportServiceCustomerPaymentResult(baseUrl);
    }

    public void fetchCustomerPaymentResult(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerPaymentResult(path, userLoginKey, customerID);
    }

    public void getSipSupportServiceAddCustomerPayments(String baseUrl) {
        repository.getSipSupportServiceAddCustomerPayments(baseUrl);
    }

    public void getSipSupportServiceEditCustomerPayments(String baseUrl) {
        repository.getSipSupportServiceEditCustomerPayments(baseUrl);
    }

    public void getSipSupportServiceDeleteCustomerPayments(String baseUrl) {
        repository.getSipSupportServiceDeleteCustomerPayments(baseUrl);
    }

    public void attach(String path, String userLoginKey, AttachInfo attachInfo) {
        repository.attach(path, userLoginKey, attachInfo);
    }

    public void addCustomerPaymentsResult(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        repository.addCustomerPayments(path, userLoginKey, customerPaymentInfo);
    }

    public void editCustomerPaymentsResult(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        repository.editCustomerPayments(path, userLoginKey, customerPaymentInfo);
    }

    public void deleteCustomerPayments(String path, String userLoginKey, int customerPaymentID) {
        repository.deleteCustomerPayments(path, userLoginKey, customerPaymentID);
    }

    public void getSipSupportServiceGetBankAccountResult(String baseUrl) {
        repository.getSipSupportServiceGetBankAccountResult(baseUrl);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }
}
