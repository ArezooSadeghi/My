package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerProducts;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerProductViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;
    private SingleLiveEvent<String> errorProductResultSingleLiveEvent;
    private SingleLiveEvent<ProductResult> getProductResultSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> PostCustomerProductsSingleLiveEvent;
    private SingleLiveEvent<String> errorPostCustomerProductsSingleLiveEvent;

    private SingleLiveEvent<ProductResult> productInfoSingleLiveEvent;
    private SingleLiveEvent<String> errorProductInfoSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> customerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<Boolean> ProductsFragmentDialogDismissSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteCustomerProductSingleLiveEvent;

    private SingleLiveEvent<Integer> deleteClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> editCustomerProductSingleLiveEvent;
    private SingleLiveEvent<String> errorEditCustomerProductSingleLiveEvent;

    private SingleLiveEvent<CustomerProducts> editClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDeleteSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnection;

    private SingleLiveEvent<CustomerProducts> mProductAdapterSeeDocumentsClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    public CustomerProductViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());

        errorProductResultSingleLiveEvent = repository.getErrorAddProductResultSingleLiveEvent();
        getProductResultSingleLiveEvent = repository.getProductsResultSingleLiveEvent();

        PostCustomerProductsSingleLiveEvent = repository.getAddCustomerProductResultSingleLiveEvent();
        errorPostCustomerProductsSingleLiveEvent = repository.getErrorAddCustomerProductResultSingleLiveEvent();
        productInfoSingleLiveEvent = repository.getProductInfoResultSingleLiveEvent();
        errorProductInfoSingleLiveEvent = repository.getErrorProductInfoResultSingleLiveEvent();

        customerProductResultSingleLiveEvent = repository.getCustomerProductsResultSingleLiveEvent();
        errorCustomerProductResultSingleLiveEvent = repository.getErrorCustomerProductsResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        deleteCustomerProductSingleLiveEvent = repository.getDeleteCustomerProductResultSingleLiveEvent();
        errorDeleteCustomerProductSingleLiveEvent = repository.getErrorDeleteCustomerProductResultSingleLiveEvent();

        editCustomerProductSingleLiveEvent = repository.getEditCustomerProductResultSingleLiveEvent();
        errorEditCustomerProductSingleLiveEvent = repository.getErrorEditCustomerProductResultSingleLiveEvent();


        noConnection = repository.getNoConnectionExceptionHappenSingleLiveEvent();

        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();

    }

    public SingleLiveEvent<String> getErrorProductsResultSingleLiveEvent() {
        return errorProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductsResultSingleLiveEvent() {
        return getProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProducts> getProductAdapterSeeDocumentsClickedSingleLiveEvent() {
        return mProductAdapterSeeDocumentsClickedSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void fetchProductResult(String path, String userLoginKey) {
        repository.fetchProductResult(path, userLoginKey);
    }

    public void getSipSupportServiceGetProductResult(String baseUrl) {
        repository.getSipSupportServiceGetProductResult(baseUrl);
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getAddProductResultSingleLiveEvent() {
        return PostCustomerProductsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddProductResultSingleLiveEvent() {
        return errorPostCustomerProductsSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoResultSingleLiveEvent() {
        return productInfoSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductInfoResultSingleLiveEvent() {
        return errorProductInfoSingleLiveEvent;
    }

    public void getSipSupportServicePostCustomerProducts(String baseUrl) {
        repository.getSipSupportServicePostCustomerProducts(baseUrl);
    }

    public void getSipSupportServiceForGetCustomerProductInfo(String baseUrl) {
        repository.getSipSupportServiceForGetProductInfo(baseUrl);
    }

    public void postCustomerProducts(String path, String userLoginKey, CustomerProducts customerProducts) {
        repository.postCustomerProducts(path, userLoginKey, customerProducts);
    }

    public void fetchProductInfo(String path, String userLoginKey, int productID) {
        repository.fetchProductInfo(path, userLoginKey, productID);
    }

    public SingleLiveEvent<CustomerProductResult> getCustomerProductsResultSingleLiveEvent() {
        return customerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerProductsResultSingleLiveEvent() {
        return errorCustomerProductResultSingleLiveEvent;
    }

    public void getSipSupportServiceGetCustomerProductResult(String baseUrl) {
        repository.getSipSupportServiceGetCustomerProductResult(baseUrl);
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public void fetchProductResult(String path, String userLoginKey, int customerID) {
        repository.fetchProductResult(path, userLoginKey, customerID);
    }

    public SingleLiveEvent<Boolean> getProductsFragmentDialogDismissSingleLiveEvent() {
        return ProductsFragmentDialogDismissSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getDeleteCustomerProductResultSingleLiveEvent() {
        return deleteCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerProductResultSingleLiveEvent() {
        return errorDeleteCustomerProductSingleLiveEvent;
    }

    public void getSipSupportServiceForDeleteCustomerProduct(String baseUrl) {
        repository.getSipSupportServiceForDeleteCustomerProduct(baseUrl);
    }

    public void deleteCustomerProduct(String path, String userLoginKey, int customerProductID) {
        repository.deleteCustomerProduct(path, userLoginKey, customerProductID);
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClickedSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProducts> getEditClicked() {
        return editClickedSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditProductResultSingleLiveEvent() {
        return editCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditProductResultSingleLiveEvent() {
        return errorEditCustomerProductSingleLiveEvent;
    }

    public void getSipSupportServiceForEditCustomerProduct(String baseUrl) {
        repository.getSipSupportServiceForEditCustomerProduct(baseUrl);
    }

    public void editCustomerProduct(String path, String userLoginKey, CustomerProducts customerProducts) {
        repository.editCustomerProduct(path, userLoginKey, customerProducts);
    }

    public SingleLiveEvent<Boolean> getYesDelete() {
        return yesDeleteSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }
}
