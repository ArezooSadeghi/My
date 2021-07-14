package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AttachmentViewModel extends AndroidViewModel {

    private SipSupporterRepository mRepository;
    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachResultViaAttachIDSingleLiveEvent;
    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent;
    private SingleLiveEvent<AttachResult> attachmentListResultByPaymentID;
    private SingleLiveEvent<Boolean> requestPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> allowPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mIsAttachAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mYesAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mNoAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> updateImageListSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> finishWriteToStorage = new SingleLiveEvent<>();
    private SingleLiveEvent<String> photoClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> hideLoading = new SingleLiveEvent<>();

    public AttachmentViewModel(@NonNull Application application) {
        super(application);

        mRepository = SipSupporterRepository.getInstance(getApplication());
        attachResultSingleLiveEvent = mRepository.getAttachResultSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = mRepository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = mRepository.getTimeoutExceptionHappenSingleLiveEvent();
        getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = mRepository.getCustomerPaymentAttachmentsResultSingleLiveEvent();
        getAttachmentFilesViaCustomerProductIDSingleLiveEvent = mRepository.getCustomerProductAttachmentsResultSingleLiveEvent();
        getAttachmentFilesViaCustomerSupportIDSingleLiveEvent = mRepository.getCustomerSupportAttachmentsResultSingleLiveEvent();
        attachResultViaAttachIDSingleLiveEvent = mRepository.getAttachResultViaAttachIDSingleLiveEvent();
        deleteAttachResultSingleLiveEvent = mRepository.getDeleteAttachResultSingleLiveEvent();
        attachmentListResultByPaymentID = mRepository.getPaymentAttachmentsResultSingleLiveEvent();
    }

    public SingleLiveEvent<Boolean> getRequestCameraPermission() {
        return requestPermission;
    }

    public SingleLiveEvent<Boolean> getAllowCameraPermissionSingleLiveEvent() {
        return allowPermission;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getShowAttachAgainDialog() {
        return mIsAttachAgainSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesAttachAgain() {
        return mYesAgainSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getNoAttachAgain() {
        return mNoAgainSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerPaymentAttachmentsResultSingleLiveEvent() {
        return getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerProductAttachmentsSingleLiveEvent() {
        return getAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerSupportAttachmentsResultSingleLiveEvent() {
        return getAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachInfoResultSingleLiveEvent() {
        return attachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getUpdatePhotoGallerySingleLiveEvent() {
        return updateImageListSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getPaymentAttachmentsResultSingleLiveEvent() {
        return attachmentListResultByPaymentID;
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
        return mRepository.getServerData(centerName);
    }

    public void getSipSupporterServiceForAddAttachment(String baseUrl) {
        mRepository.getSipSupporterServiceAttachResult(baseUrl);
    }

    public void getSipSupporterServiceAttachResult(String baseUrl) {
        mRepository.getSipSupporterServiceAttachResult(baseUrl);
    }

    public void addAttachment(String path, String userLoginKey, AttachResult.AttachInfo attachInfo) {
        mRepository.attach(path, userLoginKey, attachInfo);
    }

    public void fetchCustomerPaymentAttachments(String path, String userLoginKey, int customerPaymentID, boolean LoadFileData) {
        mRepository.fetchCustomerPaymentAttachments(path, userLoginKey, customerPaymentID, LoadFileData);
    }

    public void fetchCustomerProductAttachments(String path, String userLoginKey, int customerProductID, boolean LoadFileData) {
        mRepository.fetchCustomerProductAttachments(path, userLoginKey, customerProductID, LoadFileData);
    }

    public void fetchCustomerSupportAttachments(String path, String userLoginKey, int customerSupportID, boolean LoadFileData) {
        mRepository.fetchCustomerSupportAttachments(path, userLoginKey, customerSupportID, LoadFileData);
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID, boolean loadFileData) {
        mRepository.fetchAttachInfo(path, userLoginKey, attachID, loadFileData);
    }

    public void deleteAttachment(String path, String userLoginKey, int attachID) {
        mRepository.deleteAttach(path, userLoginKey, attachID);
    }

    public void fetchPaymentAttachments(String path, String userLoginKey, int paymentID, boolean LoadFileData) {
        mRepository.fetchPaymentAttachments(path, userLoginKey, paymentID, LoadFileData);
    }
}
