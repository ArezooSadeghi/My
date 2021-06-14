package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentInfo;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class PaymentViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent;

    private SingleLiveEvent<PaymentResult> paymentsByBankAccountResultSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentsByBankAccountResultSingleLiveEvent;

    private SingleLiveEvent<PaymentInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentInfo> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentInfo> seePaymentAttachmentsClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> addPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorAddPaymentResultSingleLiveEvent;

    private SingleLiveEvent<PaymentResult> editPaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorEditPaymentResultSingleLiveEvent;

    private SingleLiveEvent<PaymentResult> deletePaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeletePaymentResultSingleLiveEvent;

    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> updatingSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectInfoResultSingleLiveEvent;

    public PaymentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        errorBankAccountsResultSingleLiveEvent = repository.getErrorBankAccountsResultSingleLiveEvent();

        paymentsByBankAccountResultSingleLiveEvent = repository.getPaymentsResultSingleLiveEvent();
        errorPaymentsByBankAccountResultSingleLiveEvent = repository.getErrorPaymentsResultSingleLiveEvent();

        addPaymentResultSingleLiveEvent = repository.getAddPaymentResultSingleLiveEvent();
        errorAddPaymentResultSingleLiveEvent = repository.getErrorAddPaymentResultSingleLiveEvent();

        editPaymentResultSingleLiveEvent = repository.getEditPaymentResultSingleLiveEvent();
        errorEditPaymentResultSingleLiveEvent = repository.getErrorEditPaymentResultSingleLiveEvent();

        deletePaymentResultSingleLiveEvent = repository.getDeletePaymentResultSingleLiveEvent();
        errorDeletePaymentResultSingleLiveEvent = repository.getErrorDeletePaymentResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();

        paymentSubjectInfoResultSingleLiveEvent = repository.getPaymentSubjectInfoResultSingleLiveEvent();
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentsByBankAccountResultSingleLiveEvent() {
        return paymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentsByBankAccountResultSingleLiveEvent() {
        return errorPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<PaymentInfo> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<PaymentInfo> getSeePaymentAttachmentsClicked() {
        return seePaymentAttachmentsClicked;
    }

    public SingleLiveEvent<PaymentResult> getAddPaymentResultSingleLiveEvent() {
        return addPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddPaymentResultSingleLiveEvent() {
        return errorAddPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getEditPaymentResultSingleLiveEvent() {
        return editPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditPaymentResultSingleLiveEvent() {
        return errorEditPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getDeletePaymentResultSingleLiveEvent() {
        return deletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeletePaymentResultSingleLiveEvent() {
        return errorDeletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public SingleLiveEvent<Integer> getUpdatingSingleLiveEvent() {
        return updatingSingleLiveEvent;
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

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectInfoResultSingleLiveEvent() {
        return paymentSubjectInfoResultSingleLiveEvent;
    }

    public void getSipSupporterServiceBankAccountsResult(String baseUrl) {
        repository.getSipSupporterServiceBankAccountsResult(baseUrl);
    }

    public void getSipSupporterServicePaymentsByBankAccount(String baseUrl) {
        repository.getSipSupportServicePaymentsListByBankAccount(baseUrl);
    }

    public void getSipSupporterServiceAddPayment(String baseUrl) {
        repository.getSipSupporterServiceAddPayment(baseUrl);
    }

    public void getSipSupporterServiceEditPayment(String baseUrl) {
        repository.getSipSupporterServiceEditPayment(baseUrl);
    }

    public void getSipSupporterServiceDeletePayment(String baseUrl) {
        repository.getSipSupporterServiceDeletePayment(baseUrl);
    }

    public void getSipSupporterServicePaymentInfo(String baseUrl) {
        repository.getSipSupporterServicePaymentInfo(baseUrl);
    }


    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }

    public void fetchPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        repository.fetchPaymentsListByBankAccounts(path, userLoginKey, bankAccountID);
    }

    public void addPayment(String path, String userLoginKey, PaymentInfo paymentInfo) {
        repository.addPayment(path, userLoginKey, paymentInfo);
    }

    public void editPayment(String path, String userLoginKey, PaymentInfo paymentInfo) {
        repository.editPayment(path, userLoginKey, paymentInfo);
    }

    public void deletePayment(String path, String userLoginKey, int paymentID) {
        repository.deletePayment(path, userLoginKey, paymentID);
    }

    public void fetchPaymentSubjectInfo(String path, String userLoginKey, int paymentSubjectID) {
        repository.fetchPaymentSubjectInfo(path, userLoginKey, paymentSubjectID);
    }
}
