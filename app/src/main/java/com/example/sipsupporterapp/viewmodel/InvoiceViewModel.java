package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.InvoiceResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class InvoiceViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<InvoiceResult> invoiceInfoByCaseIDResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceResult> addInvoiceResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceDetailsResult> addInvoiceDetailsResultSingleLiveEvent;
    private SingleLiveEvent<ProductResult> productInfoResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceDetailsResult> invoiceDetailsResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceDetailsResult> editInvoiceDetailsResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceDetailsResult> deleteInvoiceDetailsResultSingleLiveEvent;
    private SingleLiveEvent<InvoiceDetailsResult.InvoiceDetailsInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> refresh = new SingleLiveEvent<>();

    public InvoiceViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        invoiceInfoByCaseIDResultSingleLiveEvent = repository.getInvoiceInfoByCaseIDResultSingleLiveEvent();
        addInvoiceResultSingleLiveEvent = repository.getAddInvoiceResultSingleLiveEvent();
        addInvoiceDetailsResultSingleLiveEvent = repository.getAddInvoiceDetailsResultSingleLiveEvent();
        productInfoResultSingleLiveEvent = repository.getProductInfoResultSingleLiveEvent();
        invoiceDetailsResultSingleLiveEvent = repository.getInvoiceDetailsResultSingleLiveEvent();
        editInvoiceDetailsResultSingleLiveEvent = repository.getEditInvoiceDetailsResultSingleLiveEvent();
        deleteInvoiceDetailsResultSingleLiveEvent = repository.getDeleteInvoiceDetailsResultSingleLiveEvent();
    }

    public SingleLiveEvent<InvoiceResult> getInvoiceInfoByCaseIDResultSingleLiveEvent() {
        return invoiceInfoByCaseIDResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceResult> getAddInvoiceResultSingleLiveEvent() {
        return addInvoiceResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceDetailsResult> getAddInvoiceDetailsResultSingleLiveEvent() {
        return addInvoiceDetailsResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoResultSingleLiveEvent() {
        return productInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceDetailsResult> getInvoiceDetailsResultSingleLiveEvent() {
        return invoiceDetailsResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceDetailsResult> getEditInvoiceDetailsResultSingleLiveEvent() {
        return editInvoiceDetailsResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceDetailsResult> getDeleteInvoiceDetailsResultSingleLiveEvent() {
        return deleteInvoiceDetailsResultSingleLiveEvent;
    }

    public SingleLiveEvent<InvoiceDetailsResult.InvoiceDetailsInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<Boolean> getRefresh() {
        return refresh;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceInvoiceResult(String baseUrl) {
        repository.getSipSupporterServiceInvoiceResult(baseUrl);
    }

    public void getSipSupporterServiceAddInvoiceDetailsResult(String baseUrl) {
        repository.getSipSupporterServiceAddInvoiceDetailsResult(baseUrl);
    }

    public void getSipSupporterServiceProductResult(String baseUrl) {
        repository.getSipSupporterServiceProductResult(baseUrl);
    }

    public void fetchInvoiceInfoByCaseID(String path, String userLoginKey, int caseID) {
        repository.fetchInvoiceInfoByCaseID(path, userLoginKey, caseID);
    }

    public void addInvoice(String path, String userLoginKey, InvoiceResult.InvoiceInfo invoiceInfo) {
        repository.addInvoice(path, userLoginKey, invoiceInfo);
    }

    public void addInvoiceDetails(String path, String userLoginKey, InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo) {
        repository.addInvoiceDetails(path, userLoginKey, invoiceDetailsInfo);
    }

    public void fetchProductInfo(String path, String userLoginKey, int productGroupID) {
        repository.fetchProductInfo(path, userLoginKey, productGroupID);
    }

    public void fetchInvoiceDetails(String path, String userLoginKey, int invoiceID) {
        repository.fetchInvoiceDetails(path, userLoginKey, invoiceID);
    }

    public void editInvoiceDetails(String path, String userLoginKey, InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo) {
        repository.editInvoiceDetails(path, userLoginKey, invoiceDetailsInfo);
    }

    public void deleteInvoiceDetails(String path, String userLoginKey, int invoiceDetailsID) {
        repository.deleteInvoiceDetails(path, userLoginKey, invoiceDetailsID);
    }
}
