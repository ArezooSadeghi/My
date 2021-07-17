package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.InvoiceResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class PrintViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<InvoiceDetailsResult> invoiceDetailsResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceResult> invoiceInfoResultSingleLiveEvent;

    public PrintViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        invoiceDetailsResultSingleLiveEvent = repository.getInvoiceDetailsResultSingleLiveEvent();
        invoiceInfoResultSingleLiveEvent = repository.getInvoiceInfoResultSingleLiveEvent();
    }

    public SingleLiveEvent<InvoiceDetailsResult> getInvoiceDetailsResultSingleLiveEvent() {
        return invoiceDetailsResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceResult> getInvoiceInfoResultSingleLiveEvent() {
        return invoiceInfoResultSingleLiveEvent;
    }

    public void getSipSupporterServiceInvoiceDetailsResult(String baseUrl) {
        repository.getSipSupporterServiceInvoiceDetailsResult(baseUrl);
    }

    public void getSipSupporterServiceInvoiceResult(String baseUrl) {
        repository.getSipSupporterServiceInvoiceResult(baseUrl);
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void fetchInvoiceDetails(String path, String userLoginKey, int invoiceID) {
        repository.fetchInvoiceDetails(path, userLoginKey, invoiceID);
    }

    public void fetchInvoiceInfo(String path, String userLoginKey, int invoiceID) {
        repository.fetchInvoiceInfo(path, userLoginKey, invoiceID);
    }
}
