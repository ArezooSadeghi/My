package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CustomerProductInfo;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerProductViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;

    private SingleLiveEvent<CustomerProductResult> addCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> errorAddCustomerProductResultSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> editCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> errorEditCustomerProductResultSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteCustomerProductResultSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> customerProductsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerProductsResultSingleLiveEvent;

    private SingleLiveEvent<ProductResult> productInfoResultSingleLiveEvent;
    private SingleLiveEvent<String> errorProductInfoResultSingleLiveEvent;

    private SingleLiveEvent<String> errorProductsResultSingleLiveEvent;
    private SingleLiveEvent<ProductResult> getProductsResultSingleLiveEvent;

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    private SingleLiveEvent<Boolean> dialogDismissed = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerProductInfo> seeCustomerProductAttachmentsClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    public CustomerProductViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        addCustomerProductResultSingleLiveEvent = repository.getAddCustomerProductResultSingleLiveEvent();
        errorAddCustomerProductResultSingleLiveEvent = repository.getErrorAddCustomerProductResultSingleLiveEvent();

        editCustomerProductResultSingleLiveEvent = repository.getEditCustomerProductResultSingleLiveEvent();
        errorEditCustomerProductResultSingleLiveEvent = repository.getErrorEditCustomerProductResultSingleLiveEvent();

        deleteCustomerProductResultSingleLiveEvent = repository.getDeleteCustomerProductResultSingleLiveEvent();
        errorDeleteCustomerProductResultSingleLiveEvent = repository.getErrorDeleteCustomerProductResultSingleLiveEvent();

        customerProductsResultSingleLiveEvent = repository.getCustomerProductsResultSingleLiveEvent();
        errorCustomerProductsResultSingleLiveEvent = repository.getErrorCustomerProductsResultSingleLiveEvent();

        productInfoResultSingleLiveEvent = repository.getProductInfoResultSingleLiveEvent();
        errorProductInfoResultSingleLiveEvent = repository.getErrorProductInfoResultSingleLiveEvent();

        errorProductsResultSingleLiveEvent = repository.getErrorAddProductResultSingleLiveEvent();
        getProductsResultSingleLiveEvent = repository.getProductsResultSingleLiveEvent();

        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();
    }

    public SingleLiveEvent<CustomerProductResult> getAddCustomerProductResultSingleLiveEvent() {
        return addCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerProductResultSingleLiveEvent() {
        return errorAddCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditCustomerProductResultSingleLiveEvent() {
        return editCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerProductResultSingleLiveEvent() {
        return errorEditCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getDeleteCustomerProductResultSingleLiveEvent() {
        return deleteCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerProductResultSingleLiveEvent() {
        return errorDeleteCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getCustomerProductsResultSingleLiveEvent() {
        return customerProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerProductsResultSingleLiveEvent() {
        return errorCustomerProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoResultSingleLiveEvent() {
        return productInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductInfoResultSingleLiveEvent() {
        return errorProductInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductsResultSingleLiveEvent() {
        return errorProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductsResultSingleLiveEvent() {
        return getProductsResultSingleLiveEvent;
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

    public SingleLiveEvent<Boolean> getDialogDismissed() {
        return dialogDismissed;
    }

    public SingleLiveEvent<CustomerProductInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<CustomerProductInfo> getSeeCustomerProductAttachmentsClicked() {
        return seeCustomerProductAttachmentsClicked;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceProductsResult(String baseUrl) {
        repository.getSipSupporterServiceProductsResult(baseUrl);
    }

    public void getSipSupporterServiceAddCustomerProduct(String baseUrl) {
        repository.getSipSupporterServiceAddCustomerProduct(baseUrl);
    }

    public void getSipSupporterServiceEditCustomerProduct(String baseUrl) {
        repository.getSipSupporterServiceEditCustomerProduct(baseUrl);
    }

    public void getSipSupporterServiceDeleteCustomerProduct(String baseUrl) {
        repository.getSipSupporterServiceDeleteCustomerProduct(baseUrl);
    }

    public void getSipSupporterServiceProductInfo(String baseUrl) {
        repository.getSipSupporterServiceProductInfo(baseUrl);
    }

    public void getSipSupporterServiceCustomerProductsResult(String baseUrl) {
        repository.getSipSupporterServiceCustomerProductsResult(baseUrl);
    }

    public void fetchProducts(String path, String userLoginKey) {
        repository.fetchCustomerProducts(path, userLoginKey);
    }

    public void addCustomerProduct(String path, String userLoginKey, CustomerProductInfo customerProductInfo) {
        repository.addCustomerProduct(path, userLoginKey, customerProductInfo);
    }

    public void editCustomerProduct(String path, String userLoginKey, CustomerProductInfo customerProductInfo) {
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
