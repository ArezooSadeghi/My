package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AttachmentViewModel extends AndroidViewModel {

    private SipSupporterRepository repository;
    private SingleLiveEvent<AttachResult> attachmentsByCustomerPaymentResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachmentsByCustomerProductResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachmentsByCustomerSupportResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachmentsByPaymentResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachInfoResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> requestPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> allowPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> attachAgainClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> refresh = new SingleLiveEvent<>();
    private SingleLiveEvent<String> finishWriteToStorage = new SingleLiveEvent<>();
    private SingleLiveEvent<String> photoClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> hideLoading = new SingleLiveEvent<>();

    public AttachmentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        attachmentsByCustomerPaymentResultSingleLiveEvent = repository.getCustomerPaymentAttachmentsResultSingleLiveEvent();
        attachmentsByCustomerProductResultSingleLiveEvent = repository.getCustomerProductAttachmentsResultSingleLiveEvent();
        attachmentsByCustomerSupportResultSingleLiveEvent = repository.getCustomerSupportAttachmentsResultSingleLiveEvent();
        attachmentsByPaymentResultSingleLiveEvent = repository.getPaymentAttachmentsResultSingleLiveEvent();
        attachInfoResultSingleLiveEvent = repository.getAttachResultViaAttachIDSingleLiveEvent();
        attachResultSingleLiveEvent = repository.getAttachResultSingleLiveEvent();
        deleteAttachResultSingleLiveEvent = repository.getDeleteAttachResultSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<AttachResult> getAttachmentsByCustomerPaymentResultSingleLiveEvent() {
        return attachmentsByCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachmentsByCustomerProductResultSingleLiveEvent() {
        return attachmentsByCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachmentsByCustomerSupportResultSingleLiveEvent() {
        return attachmentsByCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachmentsByPaymentResultSingleLiveEvent() {
        return attachmentsByPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachInfoResultSingleLiveEvent() {
        return attachInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRequestPermission() {
        return requestPermission;
    }

    public SingleLiveEvent<Boolean> getAllowPermission() {
        return allowPermission;
    }

    public SingleLiveEvent<Boolean> getAttachAgain() {
        return attachAgainClicked;
    }

    public SingleLiveEvent<Boolean> getYesAttachAgainClicked() {
        return yesClicked;
    }

    public SingleLiveEvent<Boolean> getNoAttachAgainClicked() {
        return noClicked;
    }

    public SingleLiveEvent<AttachResult> getRefresh() {
        return refresh;
    }

    public SingleLiveEvent<String> getFinishWriteToStorage() {
        return finishWriteToStorage;
    }

    public SingleLiveEvent<String> getPhotoClicked() {
        return photoClicked;
    }

    public SingleLiveEvent<Boolean> getHideLoading() {
        return hideLoading;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceAttachResult(String baseUrl) {
        repository.getSipSupporterServiceAttachResult(baseUrl);
    }

    public void attach(String path, String userLoginKey, AttachResult.AttachInfo attachInfo) {
        repository.attach(path, userLoginKey, attachInfo);
    }

    public void fetchCustomerPaymentAttachments(String path, String userLoginKey, int customerPaymentID, boolean LoadFileData) {
        repository.fetchCustomerPaymentAttachments(path, userLoginKey, customerPaymentID, LoadFileData);
    }

    public void fetchCustomerProductAttachments(String path, String userLoginKey, int customerProductID, boolean LoadFileData) {
        repository.fetchCustomerProductAttachments(path, userLoginKey, customerProductID, LoadFileData);
    }

    public void fetchCustomerSupportAttachments(String path, String userLoginKey, int customerSupportID, boolean LoadFileData) {
        repository.fetchCustomerSupportAttachments(path, userLoginKey, customerSupportID, LoadFileData);
    }

    public void fetchPaymentAttachments(String path, String userLoginKey, int paymentID, boolean LoadFileData) {
        repository.fetchPaymentAttachments(path, userLoginKey, paymentID, LoadFileData);
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID, boolean loadFileData) {
        repository.fetchAttachInfo(path, userLoginKey, attachID, loadFileData);
    }

    public void deleteAttachment(String path, String userLoginKey, int attachID) {
        repository.deleteAttach(path, userLoginKey, attachID);
    }
}
