package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AttachmentViewModel extends AndroidViewModel {

    private SipSupporterRepository mRepository;

    private SingleLiveEvent<Boolean> requestPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> allowPermission = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent;
    private SingleLiveEvent<String> errorAttachResultSingleLiveEvent;

    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> mDangerousUserSingleLiveEvent;

    private SingleLiveEvent<Boolean> mIsAttachAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mYesAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mNoAgainSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent;

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent;

    private SingleLiveEvent<AttachResult> attachResultViaAttachIDSingleLiveEvent;
    private SingleLiveEvent<String> errorAttachResultViaAttachIDSingleLiveEvent;

    private SingleLiveEvent<AttachResult> updateImageListSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDelete = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteAttachResultSingleLiveEvent;

    private SingleLiveEvent<AttachResult> attachmentListResultByPaymentID;
    private SingleLiveEvent<String> errorAttachmentListResultByPaymentID;

    private SingleLiveEvent<String> finishWriteToStorage = new SingleLiveEvent<>();

    private SingleLiveEvent<String> photoClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> hideLoading = new SingleLiveEvent<>();

    public AttachmentViewModel(@NonNull Application application) {
        super(application);

        mRepository = SipSupporterRepository.getInstance(getApplication());

        attachResultSingleLiveEvent = mRepository.getAttachResultSingleLiveEvent();
        errorAttachResultSingleLiveEvent = mRepository.getErrorAttachResultSingleLiveEvent();
        noConnectionExceptionHappenSingleLiveEvent = mRepository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = mRepository.getTimeoutExceptionHappenSingleLiveEvent();
        mDangerousUserSingleLiveEvent = mRepository.getDangerousUserSingleLiveEvent();

        getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = mRepository.getCustomerPaymentAttachmentsResultSingleLiveEvent();
        getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = mRepository.getErrorCustomerPaymentAttachmentsResultSingleLiveEvent();

        getAttachmentFilesViaCustomerProductIDSingleLiveEvent = mRepository.getCustomerProductAttachmentsResultSingleLiveEvent();
        getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent = mRepository.getErrorCustomerProductAttachmentsResultSingleLiveEvent();

        getAttachmentFilesViaCustomerSupportIDSingleLiveEvent = mRepository.getCustomerSupportAttachmentsResultSingleLiveEvent();
        getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent = mRepository.getErrorCustomerSupportAttachmentsResultSingleLiveEvent();

        attachResultViaAttachIDSingleLiveEvent = mRepository.getAttachResultViaAttachIDSingleLiveEvent();
        errorAttachResultViaAttachIDSingleLiveEvent = mRepository.getErrorAttachResultViaAttachIDSingleLiveEvent();

        deleteAttachResultSingleLiveEvent = mRepository.getDeleteAttachResultSingleLiveEvent();
        errorDeleteAttachResultSingleLiveEvent = mRepository.getErrorDeleteAttachResultSingleLiveEvent();

        attachmentListResultByPaymentID = mRepository.getPaymentAttachmentsResultSingleLiveEvent();
        errorAttachmentListResultByPaymentID = mRepository.getErrorPaymentAttachmentsResultSingleLiveEvent();
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

    public SingleLiveEvent<String> getErrorAttachResultSingleLiveEvent() {
        return errorAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return mDangerousUserSingleLiveEvent;
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

    public SingleLiveEvent<String> getErrorCustomerPaymentAttachmentsResultSingleLiveEvent() {
        return getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerProductAttachmentsSingleLiveEvent() {
        return getAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerProductAttachmentsSingleLiveEvent() {
        return getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerSupportAttachmentsResultSingleLiveEvent() {
        return getAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerSupportAttachmentsResultSingleLiveEvent() {
        return getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachInfoResultSingleLiveEvent() {
        return attachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachInfoResultSingleLiveEvent() {
        return errorAttachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getUpdatePhotoGallerySingleLiveEvent() {
        return updateImageListSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesDelete() {
        return yesDelete;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteAttachResultSingleLiveEvent() {
        return errorDeleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getPaymentAttachmentsResultSingleLiveEvent() {
        return attachmentListResultByPaymentID;
    }

    public SingleLiveEvent<String> getErrorPaymentAttachmentsResultSingleLiveEvent() {
        return errorAttachmentListResultByPaymentID;
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
        mRepository.getSipSupportServiceAttach(baseUrl);
    }

    public void getSipSupporterServiceForCustomerPaymentAttachments(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFilesViaCustomerPaymentID(baseUrl);
    }

    public void getSipSupporterServiceForCustomerProductAttachments(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFilesViaCustomerProductID(baseUrl);
    }

    public void getSipSupporterServiceForCustomerSupportAttachments(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFilesViaCustomerSupportID(baseUrl);
    }

    public void getSipSupporterServiceForAttachInfo(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFileViaAttachIDRetrofitInstance(baseUrl);
    }

    public void addAttachment(String path, String userLoginKey, AttachInfo attachInfo) {
        mRepository.attach(path, userLoginKey, attachInfo);
    }

    public void fetchCustomerPaymentAttachments(String path, String userLoginKey, int customerPaymentID, boolean LoadFileData) {
        mRepository.getAttachmentFilesViaCustomerPaymentID(path, userLoginKey, customerPaymentID, LoadFileData);
    }

    public void fetchCustomerProductAttachments(String path, String userLoginKey, int customerProductID, boolean LoadFileData) {
        mRepository.fetchFileWithCustomerProductID(path, userLoginKey, customerProductID, LoadFileData);
    }

    public void fetchCustomerSupportAttachments(String path, String userLoginKey, int customerSupportID, boolean LoadFileData) {
        mRepository.fetchFileWithCustomerSupportID(path, userLoginKey, customerSupportID, LoadFileData);
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID, boolean loadFileData) {
        mRepository.fetchWithAttachID(path, userLoginKey, attachID, loadFileData);
    }

    public void deleteAttachment(String path, String userLoginKey, int attachID) {
        mRepository.deleteAttach(path, userLoginKey, attachID);
    }

    public void getSipSupporterServiceForDeleteAttachment(String baseUrl) {
        mRepository.getSipSupportServiceDeleteAttach(baseUrl);
    }

    public void getSipSupporterServiceForPaymentAttachments(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentListByPaymentID(baseUrl);
    }

    public void fetchPaymentAttachments(String path, String userLoginKey, int paymentID, boolean LoadFileData) {
        mRepository.fetchAttachmentsByPaymentID(path, userLoginKey, paymentID, LoadFileData);
    }
}
