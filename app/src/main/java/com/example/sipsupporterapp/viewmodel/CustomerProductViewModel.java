package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerProductViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<CustomerProductResult> customerProductsResultSingleLiveEvent;
    private SingleLiveEvent<CustomerProductResult> addCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<CustomerProductResult> editCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<ProductResult> productInfoResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> closeClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerProductResult.CustomerProductInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerProductResult.CustomerProductInfo> seeCustomerProductAttachmentsClicked = new SingleLiveEvent<>();

    public CustomerProductViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        customerProductsResultSingleLiveEvent = repository.getCustomerProductsResultSingleLiveEvent();
        addCustomerProductResultSingleLiveEvent = repository.getAddCustomerProductResultSingleLiveEvent();
        editCustomerProductResultSingleLiveEvent = repository.getEditCustomerProductResultSingleLiveEvent();
        deleteCustomerProductResultSingleLiveEvent = repository.getDeleteCustomerProductResultSingleLiveEvent();
        productInfoResultSingleLiveEvent = repository.getProductInfoResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerProductResult> getCustomerProductsResultSingleLiveEvent() {
        return customerProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getAddCustomerProductResultSingleLiveEvent() {
        return addCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditCustomerProductResultSingleLiveEvent() {
        return editCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getDeleteCustomerProductResultSingleLiveEvent() {
        return deleteCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoResultSingleLiveEvent() {
        return productInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getCloseClicked() {
        return closeClicked;
    }

    public SingleLiveEvent<CustomerProductResult.CustomerProductInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<CustomerProductResult.CustomerProductInfo> getSeeCustomerProductAttachmentsClicked() {
        return seeCustomerProductAttachmentsClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCustomerProductResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerProductResult(baseUrl);
    }

    public void getSipSupporterServiceProductResult(String baseUrl) {
        repository.getSipSupporterServiceProductResult(baseUrl);
    }

    public void addCustomerProduct(String path, String userLoginKey, CustomerProductResult.CustomerProductInfo customerProductInfo) {
        repository.addCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    public void editCustomerProduct(String path, String userLoginKey, CustomerProductResult.CustomerProductInfo customerProductInfo) {
        repository.editCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    public void deleteCustomerProduct(String path, String userLoginKey, int customerProductID) {
        repository.deleteCustomerProduct(path, userLoginKey, customerProductID);
    }

    public void fetchProductInfo(String path, String userLoginKey, int productID) {
        repository.fetchProductInfo(path, userLoginKey, productID);
    }

    public void fetchCustomerProducts(String path, String userLoginKey, int customerID) {
        repository.fetchCustomerProducts(path, userLoginKey, customerID);
    }
}
