package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.ProductGroupResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class ProductsViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<ProductGroupResult> productGroupsResultSingleLiveEvent;
    private SingleLiveEvent<String> error;

    public ProductsViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        productGroupsResultSingleLiveEvent = repository.getProductGroupsResultSingleLiveEvent();
        error = repository.getError();
    }

    public SingleLiveEvent<ProductGroupResult> getProductGroupsResultSingleLiveEvent() {
        return productGroupsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getError() {
        return error;
    }

    public ServerData getServerDate(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceProductGroupResult(String baseUrl) {
        repository.getSipSupporterServiceProductGroupResult(baseUrl);
    }

    public void fetchProductGroups(String path, String userLoginKey) {
        repository.fetchProductGroups(path, userLoginKey);
    }
}
