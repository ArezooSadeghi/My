package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerProducts;
import com.example.sipsupporterapp.model.ProductInfo;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CustomerProductViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;
    private SingleLiveEvent<ProductResult> productResultSingleLiveEvent;
    private SingleLiveEvent<String> errorProductResultSingleLiveEvent;
    private SingleLiveEvent<ProductResult> getProductResultSingleLiveEvent;
    private SingleLiveEvent<String> getErrorProductResultSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> PostCustomerProductsSingleLiveEvent;
    private SingleLiveEvent<String> errorPostCustomerProductsSingleLiveEvent;

    private SingleLiveEvent<ProductResult> productInfoSingleLiveEvent;
    private SingleLiveEvent<String> errorProductInfoSingleLiveEvent;

    private SingleLiveEvent<CustomerProductResult> customerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> errorCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<Boolean> dialogDismissSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> ProductsFragmentDialogDismissSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteCustomerProductSingleLiveEvent;

    private SingleLiveEvent<Integer> deleteClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> dismissSuccessfulDeleteDialogSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> editCustomerProductSingleLiveEvent;
    private SingleLiveEvent<String> errorEditCustomerProductSingleLiveEvent;

    private SingleLiveEvent<CustomerProducts> editClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> dismissSuccessfulEditDialogSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDeleteSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProducts> attachFileSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent;
    private SingleLiveEvent<String> errorAttachResultSingleLiveEvent;

    private SingleLiveEvent<Boolean> dismissAttachSuccessfulDialogSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> requestPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> allowPermission = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnection;

    private SingleLiveEvent<String> fileDataSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesAttachAgainSuccessfulDialogSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesAttachAgainProductFragmentSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noAttachAgainSuccessfulDialogSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noAttachAgainProductFragmentSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProducts> mProductAdapterSeeDocumentsClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProducts> mProductsAdapterSeeDocumentsClickedSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent;

    public CustomerProductViewModel(@NonNull Application application) {
        super(application);
        repository = SipSupporterRepository.getInstance(getApplication());
        productResultSingleLiveEvent = repository.getProductResultSingleLiveEvent();
        errorProductResultSingleLiveEvent = repository.getErrorProductResultSingleLiveEvent();
        getProductResultSingleLiveEvent = repository.getGetProductResultSingleLiveEvent();
        getErrorProductResultSingleLiveEvent = repository.getGetErrorProductResultSingleLiveEvent();
        PostCustomerProductsSingleLiveEvent = repository.getPostCustomerProductsSingleLiveEvent();
        errorPostCustomerProductsSingleLiveEvent = repository.getErrorPostCustomerProductsSingleLiveEvent();
        productInfoSingleLiveEvent = repository.getProductInfoSingleLiveEvent();
        errorProductInfoSingleLiveEvent = repository.getErrorProductInfoSingleLiveEvent();

        customerProductResultSingleLiveEvent = repository.getCustomerProductResultSingleLiveEvent();
        errorCustomerProductResultSingleLiveEvent = repository.getErrorCustomerProductResultSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        deleteCustomerProductSingleLiveEvent = repository.getDeleteCustomerProductSingleLiveEvent();
        errorDeleteCustomerProductSingleLiveEvent = repository.getErrorDeleteCustomerProductSingleLiveEvent();

        editCustomerProductSingleLiveEvent = repository.getEditCustomerProductSingleLiveEvent();
        errorEditCustomerProductSingleLiveEvent = repository.getErrorEditCustomerProductSingleLiveEvent();

        attachResultSingleLiveEvent = repository.getAttachResultSingleLiveEvent();
        errorAttachResultSingleLiveEvent = repository.getErrorAttachResultSingleLiveEvent();

        noConnection = repository.getNoConnection();

        dangerousUserSingleLiveEvent = repository.getDangerousUserSingleLiveEvent();

    }

    public SingleLiveEvent<ProductResult> getProductResultSingleLiveEvent() {
        return productResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductsResultSingleLiveEvent() {
        return errorProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductsResultSingleLiveEvent() {
        return getProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getGetErrorProductResultSingleLiveEvent() {
        return getErrorProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDialogDismissSingleLiveEvent() {
        return dialogDismissSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProducts> getProductAdapterSeeDocumentsClickedSingleLiveEvent() {
        return mProductAdapterSeeDocumentsClickedSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void postProductInfo(String path, String userLoginKey, ProductInfo productInfo) {
        repository.postProductInfo(path, userLoginKey, productInfo);
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

    public SingleLiveEvent<Boolean> getDismissSuccessfulDeleteDialogSingleLiveEvent() {
        return dismissSuccessfulDeleteDialogSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProducts> getEditClicked() {
        return editClickedSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDismissSuccessfulEditDialogSingleLiveEvent() {
        return dismissSuccessfulEditDialogSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditProductResultSingleLiveEvent() {
        return editCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditProductResultSingleLiveEvent() {
        return errorEditCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProducts> getProductsAdapterSeeDocumentsClickedSingleLiveEvent() {
        return mProductsAdapterSeeDocumentsClickedSingleLiveEvent;
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

    public SingleLiveEvent<CustomerProducts> getAttachFileSingleLiveEvent() {
        return attachFileSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachResultSingleLiveEvent() {
        return errorAttachResultSingleLiveEvent;
    }

    public void getSipSupportServiceAttach(String baseUrl) {
        repository.getSipSupportServiceAttach(baseUrl);
    }

    public void attach(String path, String userLoginKey, AttachInfo attachInfo) {
        repository.attach(path, userLoginKey, attachInfo);
    }

    public SingleLiveEvent<Boolean> getDismissAttachSuccessfulDialogSingleLiveEvent() {
        return dismissAttachSuccessfulDialogSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRequestPermission() {
        return requestPermission;
    }

    public SingleLiveEvent<Boolean> getAllowPermission() {
        return allowPermission;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<String> getFileDataSingleLiveEvent() {
        return fileDataSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesAttachAgainSuccessfulDialogSingleLiveEvent() {
        return yesAttachAgainSuccessfulDialogSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getNoAttachAgainSuccessfulDialogSingleLiveEvent() {
        return noAttachAgainSuccessfulDialogSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getNoAttachAgainProductFragmentSingleLiveEvent() {
        return noAttachAgainProductFragmentSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesAttachAgainProductFragmentSingleLiveEvent() {
        return yesAttachAgainProductFragmentSingleLiveEvent;
    }
}
