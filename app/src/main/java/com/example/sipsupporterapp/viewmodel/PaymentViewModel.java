package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class PaymentViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<PaymentResult> paymentsByBankAccountResultSingleLiveEvent;
    private SingleLiveEvent<PaymentResult> paymentInfoResultSingleLiveEvent;
    private SingleLiveEvent<PaymentResult> addPaymentResultSingleLiveEvent;
    private SingleLiveEvent<PaymentResult> editPaymentResultSingleLiveEvent;
    private SingleLiveEvent<PaymentResult> deletePaymentResultSingleLiveEvent;
    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectInfoResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<PaymentResult.PaymentInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentResult.PaymentInfo> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentResult.PaymentInfo> seePaymentAttachmentsClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> refresh = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> addNewPaymentClicked = new SingleLiveEvent<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        paymentsByBankAccountResultSingleLiveEvent = repository.getPaymentsResultSingleLiveEvent();
        paymentInfoResultSingleLiveEvent = repository.getPaymentInfoResultSingleLiveEvent();
        addPaymentResultSingleLiveEvent = repository.getAddPaymentResultSingleLiveEvent();
        editPaymentResultSingleLiveEvent = repository.getEditPaymentResultSingleLiveEvent();
        deletePaymentResultSingleLiveEvent = repository.getDeletePaymentResultSingleLiveEvent();
        paymentSubjectInfoResultSingleLiveEvent = repository.getPaymentSubjectInfoResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentsByBankAccountResultSingleLiveEvent() {
        return paymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentInfoResultSingleLiveEvent() {
        return paymentInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getAddPaymentResultSingleLiveEvent() {
        return addPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getEditPaymentResultSingleLiveEvent() {
        return editPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getDeletePaymentResultSingleLiveEvent() {
        return deletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectInfoResultSingleLiveEvent() {
        return paymentSubjectInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult.PaymentInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<PaymentResult.PaymentInfo> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<PaymentResult.PaymentInfo> getSeePaymentAttachmentsClicked() {
        return seePaymentAttachmentsClicked;
    }

    public SingleLiveEvent<Integer> getRefresh() {
        return refresh;
    }

    public SingleLiveEvent<Boolean> getAddNewPaymentClicked() {
        return addNewPaymentClicked;
    }

    public void getSipSupporterServiceBankAccountResult(String baseUrl) {
        repository.getSipSupporterServiceBankAccountResult(baseUrl);
    }

    public void getSipSupporterServicePaymentResult(String baseUrl) {
        repository.getSipSupporterServicePaymentResult(baseUrl);
    }

    public void getSipSupporterServicePaymentSubjectResult(String baseUrl) {
        repository.getSipSupporterServicePaymentSubjectResult(baseUrl);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }

    public void fetchPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        repository.fetchPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    public void fetchPaymentInfo(String path, String userLoginKey, int paymentID) {
        repository.fetchPaymentInfo(path, userLoginKey, paymentID);
    }

    public void addPayment(String path, String userLoginKey, PaymentResult.PaymentInfo paymentInfo) {
        repository.addPayment(path, userLoginKey, paymentInfo);
    }

    public void editPayment(String path, String userLoginKey, PaymentResult.PaymentInfo paymentInfo) {
        repository.editPayment(path, userLoginKey, paymentInfo);
    }

    public void deletePayment(String path, String userLoginKey, int paymentID) {
        repository.deletePayment(path, userLoginKey, paymentID);
    }

    public void fetchPaymentSubjectInfo(String path, String userLoginKey, int paymentSubjectID) {
        repository.fetchPaymentSubjectInfo(path, userLoginKey, paymentSubjectID);
    }
}
