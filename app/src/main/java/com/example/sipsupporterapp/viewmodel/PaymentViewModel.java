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

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsListByBankAccountSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentResultPaymentsListByBankAccountSingleLiveEvent;

    private SingleLiveEvent<PaymentInfo> editClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentInfo> deleteClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentInfo> seeDocumentsClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsEditSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentResultPaymentsEditSingleLiveEvent;

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsDeleteSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentResultPaymentsDeleteSingleLiveEvent;

    private SingleLiveEvent<Boolean> yesDeleteSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsAddSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentResultPaymentsAddSingleLiveEvent;

    private SingleLiveEvent<Integer> updatingSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectInfoResultSingleLiveEvent;

    public PaymentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        errorBankAccountsResultSingleLiveEvent = repository.getErrorBankAccountsResultSingleLiveEvent();

        paymentResultPaymentsListByBankAccountSingleLiveEvent = repository.getPaymentsResultSingleLiveEvent();
        errorPaymentResultPaymentsListByBankAccountSingleLiveEvent = repository.getErrorPaymentsResultSingleLiveEvent();

        paymentResultPaymentsEditSingleLiveEvent = repository.getEditPaymentResultSingleLiveEvent();
        errorPaymentResultPaymentsEditSingleLiveEvent = repository.getErrorEditPaymentResultSingleLiveEvent();

        paymentResultPaymentsDeleteSingleLiveEvent = repository.getDeletePaymentResultSingleLiveEvent();
        errorPaymentResultPaymentsDeleteSingleLiveEvent = repository.getErrorDeletePaymentResultSingleLiveEvent();

        paymentResultPaymentsAddSingleLiveEvent = repository.getAddPaymentResultSingleLiveEvent();
        errorPaymentResultPaymentsAddSingleLiveEvent = repository.getErrorAddPaymentResultSingleLiveEvent();

        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();

        paymentSubjectInfoResultSingleLiveEvent = repository.getPaymentSubjectInfoResultSingleLiveEvent();
    }

    public SingleLiveEvent<PaymentResult> getPaymentsByBankAccountIDResultSingleLiveEvent() {
        return paymentResultPaymentsListByBankAccountSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentsByBankAccountIDResultSingleLiveEvent() {
        return errorPaymentResultPaymentsListByBankAccountSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentInfo> getEditClickedSingleLiveEvent() {
        return editClickedSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentInfo> getDeleteClicked() {
        return deleteClickedSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentInfo> getSeeDocumentsClicked() {
        return seeDocumentsClickedSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getEditPaymentResultSingleLiveEvent() {
        return paymentResultPaymentsEditSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditPaymentResultSingleLiveEvent() {
        return errorPaymentResultPaymentsEditSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentDeleteResultSingleLiveEvent() {
        return paymentResultPaymentsDeleteSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentDeleteResultSingleLiveEvent() {
        return errorPaymentResultPaymentsDeleteSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesDelete() {
        return yesDeleteSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getAddPaymentResultSingleLiveEvent() {
        return paymentResultPaymentsAddSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddPaymentResultSingleLiveEvent() {
        return errorPaymentResultPaymentsAddSingleLiveEvent;
    }

    public SingleLiveEvent<Integer> getUpdateCostListSingleLiveEvent() {
        return updatingSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectInfoResultSingleLiveEvent() {
        return paymentSubjectInfoResultSingleLiveEvent;
    }

    public void getSipSupportServiceGetBankAccountResult(String baseUrl) {
        repository.getSipSupporterServiceGetBankAccountsResult(baseUrl);
    }

    public void getSipSupportServicePaymentsListByBankAccount(String baseUrl) {
        repository.getSipSupportServicePaymentsListByBankAccount(baseUrl);
    }

    public void getSipSupportServicePaymentsEdit(String baseUrl) {
        repository.getSipSupportServicePaymentsEdit(baseUrl);
    }

    public void getSipSupportServicePaymentsDelete(String baseUrl) {
        repository.getSipSupportServicePaymentsDelete(baseUrl);
    }

    public void getSipSupportServicePaymentsAdd(String baseUrl) {
        repository.getSipSupportServicePaymentsAdd(baseUrl);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }

    public void fetchPaymentsListByBankAccounts(String path, String userLoginKey, int bankAccountID) {
        repository.fetchPaymentsListByBankAccounts(path, userLoginKey, bankAccountID);
    }

    public void paymentsDelete(String path, String userLoginKey, int paymentID) {
        repository.paymentsDelete(path, userLoginKey, paymentID);
    }

    public void paymentEdit(String path, String userLoginKey, PaymentInfo paymentInfo) {
        repository.paymentsEdit(path, userLoginKey, paymentInfo);
    }

    public void paymentsAdd(String path, String userLoginKey, PaymentInfo paymentInfo) {
        repository.paymentsAdd(path, userLoginKey, paymentInfo);
    }

    public void getSipSupporterServicePaymentInfo(String baseUrl) {
        repository.getSipSupporterServicePaymentInfo(baseUrl);
    }

    public void fetchPaymentSubjectInfo(String path, String userLoginKey, int paymentSubjectID) {
        repository.fetchPaymentSubjectInfo(path, userLoginKey, paymentSubjectID);
    }
}
