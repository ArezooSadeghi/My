package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentInfo;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class NewPaymentViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<PaymentResult> paymentsByBankAccountResultSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentsByBankAccountResultSingleLiveEvent;

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent;

    private SingleLiveEvent<PaymentResult> deletePaymentResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeletePaymentResultSingleLiveEvent;

    private SingleLiveEvent<PaymentInfo> editClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentInfo> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<PaymentInfo> seeDocumentsClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesDelete = new SingleLiveEvent();


    public NewPaymentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        paymentsByBankAccountResultSingleLiveEvent = repository.getPaymentsByBankAccountResultSingleLiveEvent();
        errorPaymentsByBankAccountResultSingleLiveEvent = repository.getErrorPaymentsByBankAccountResultSingleLiveEvent();

        bankAccountsResultSingleLiveEvent = repository.getBankAccountsResultSingleLiveEvent();
        errorBankAccountsResultSingleLiveEvent = repository.getErrorBankAccountsResultSingleLiveEvent();

        deletePaymentResultSingleLiveEvent = repository.getDeletePaymentResultSingleLiveEvent();
        errorDeletePaymentResultSingleLiveEvent = repository.getErrorDeletePaymentResultSingleLiveEvent();
    }

    public SingleLiveEvent<PaymentResult> getPaymentsByBankAccountResultSingleLiveEvent() {
        return paymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentsByBankAccountResultSingleLiveEvent() {
        return errorPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentInfo> getEditClickedSingleLiveEvent() {
        return editClickedSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentInfo> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<PaymentInfo> getSeeDocumentsClicked() {
        return seeDocumentsClicked;
    }

    public SingleLiveEvent<Boolean> getYesDelete() {
        return yesDelete;
    }

    public SingleLiveEvent<PaymentResult> getDeletePaymentResultSingleLiveEvent() {
        return deletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeletePaymentResultSingleLiveEvent() {
        return errorDeletePaymentResultSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServicePaymentsByBankAccount(String baseUrl) {
        repository.getSipSupporterServicePaymentsByBankAccount(baseUrl);
    }

    public void getSipSupportServiceGetBankAccountResult(String baseUrl) {
        repository.getSipSupportServiceGetBankAccountResult(baseUrl);
    }

    public void getSipSupportServicePaymentsDelete(String baseUrl) {
        repository.getSipSupportServicePaymentsDelete(baseUrl);
    }

    public void paymentsDelete(String path, String userLoginKey, int paymentID) {
        repository.paymentsDelete(path, userLoginKey, paymentID);
    }

    public void fetchPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        repository.fetchPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        repository.fetchBankAccounts(path, userLoginKey);
    }
}
