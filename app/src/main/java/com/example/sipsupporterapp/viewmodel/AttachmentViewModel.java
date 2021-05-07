package com.example.sipsupporterapp.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupportRepository;

import java.util.List;
import java.util.Map;

public class AttachmentViewModel extends AndroidViewModel {
    private SipSupportRepository mRepository;

    private SingleLiveEvent<Boolean> mRequestPermissionSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mAllowPermissionSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> mAttachResultSingleLiveEvent;
    private SingleLiveEvent<String> mErrorAttachResultSingleLiveEvent;
    private SingleLiveEvent<String> mNoConnectionSingleLiveEvent;
    private SingleLiveEvent<Boolean> mTimeOutExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<Boolean> mDangerousUserSingleLiveEvent;

    private SingleLiveEvent<String> mBitmapAsStringSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> mIsAttachAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mYesAgainSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mNoAgainSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;

    private SingleLiveEvent<AttachInfo[]> doneWriteFilesSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultForImageListSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent;

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent;

    private SingleLiveEvent<Bitmap> attachmentAdapterItemClickedSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> showFullScreenSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultViaAttachIDSingleLiveEvent;
    private SingleLiveEvent<String> errorAttachResultViaAttachIDSingleLiveEvent;

    private SingleLiveEvent<AttachResult> updateImageListSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> bitmapIsSetSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<List<Bitmap>> bitmapListSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDelete = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent;
    private SingleLiveEvent<String> errorDeleteAttachResultSingleLiveEvent;

    private SingleLiveEvent<Map<Uri, String>> showFullScreenImage = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachmentListResultByPaymentID;
    private SingleLiveEvent<String> errorAttachmentListResultByPaymentID;

    private SingleLiveEvent<Bitmap> decodeBitmap = new SingleLiveEvent<>();

    public AttachmentViewModel(@NonNull Application application) {
        super(application);

        mRepository = SipSupportRepository.getInstance(getApplication());

        mAttachResultSingleLiveEvent = mRepository.getAttachResultSingleLiveEvent();
        mErrorAttachResultSingleLiveEvent = mRepository.getErrorAttachResultSingleLiveEvent();
        mNoConnectionSingleLiveEvent = mRepository.getNoConnection();
        mTimeOutExceptionHappenSingleLiveEvent = mRepository.getTimeoutExceptionHappen();
        mDangerousUserSingleLiveEvent = mRepository.getDangerousUserSingleLiveEvent();

        getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = mRepository.getGetAttachmentFilesViaCustomerPaymentIDSingleLiveEvent();
        getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = mRepository.getGetErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent();

        getAttachmentFilesViaCustomerProductIDSingleLiveEvent = mRepository.getGetAttachmentFilesViaCustomerProductIDSingleLiveEvent();
        getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent = mRepository.getGetErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent();

        getAttachmentFilesViaCustomerSupportIDSingleLiveEvent = mRepository.getGetAttachmentFilesViaCustomerSupportIDSingleLiveEvent();
        getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent = mRepository.getGetErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent();

        attachResultViaAttachIDSingleLiveEvent = mRepository.getAttachResultViaAttachIDSingleLiveEvent();
        errorAttachResultViaAttachIDSingleLiveEvent = mRepository.getErrorAttachResultViaAttachIDSingleLiveEvent();

        deleteAttachResultSingleLiveEvent = mRepository.getDeleteAttachResultSingleLiveEvent();
        errorDeleteAttachResultSingleLiveEvent = mRepository.getErrorDeleteAttachResultSingleLiveEvent();

        attachmentListResultByPaymentID = mRepository.getAttachmentListResultByPaymentID();
        errorAttachmentListResultByPaymentID = mRepository.getErrorAttachmentListResultByPaymentID();
    }

    public SingleLiveEvent<Boolean> getRequestCameraPermission() {
        return mRequestPermissionSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getAllowCameraPermissionSingleLiveEvent() {
        return mAllowPermissionSingleLiveEvent;
    }

    public SingleLiveEvent<String> getBitmapAsStringSingleLiveEvent() {
        return mBitmapAsStringSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return mAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachResultSingleLiveEvent() {
        return mErrorAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionSingleLiveEvent() {
        return mNoConnectionSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getTimeOutExceptionHappenSingleLiveEvent() {
        return mTimeOutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return mDangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getShowAttachAgainDialog() {
        return mIsAttachAgainSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesAgain() {
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

    public SingleLiveEvent<AttachInfo[]> getDoneWriteFilesSingleLiveEvent() {
        return doneWriteFilesSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultForImageListSingleLiveEvent() {
        return attachResultForImageListSingleLiveEvent;
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

    public SingleLiveEvent<Bitmap> getAttachmentAdapterItemClickedSingleLiveEvent() {
        return attachmentAdapterItemClickedSingleLiveEvent;
    }

    public SingleLiveEvent<String> getShowFullScreenSingleLiveEvent() {
        return showFullScreenSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachInfoResultSingleLiveEvent() {
        return attachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachInfoResultSingleLiveEvent() {
        return errorAttachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getUpdateImageListSingleLiveEvent() {
        return updateImageListSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getBitmapIsSetSingleLiveEvent() {
        return bitmapIsSetSingleLiveEvent;
    }

    public SingleLiveEvent<List<Bitmap>> getBitmapListSingleLiveEvent() {
        return bitmapListSingleLiveEvent;
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

    public SingleLiveEvent<Map<Uri, String>> getShowFullScreenPhoto() {
        return showFullScreenImage;
    }

    public SingleLiveEvent<AttachResult> getPaymentAttachmentsResultSingleLiveEvent() {
        return attachmentListResultByPaymentID;
    }

    public SingleLiveEvent<String> getErrorPaymentAttachmentsResultSingleLiveEvent() {
        return errorAttachmentListResultByPaymentID;
    }

    public SingleLiveEvent<Bitmap> getDecodeBitmap() {
        return decodeBitmap;
    }

    public ServerData getServerData(String centerName) {
        return mRepository.getServerData(centerName);
    }

    public void getSipSupportServiceAttach(String baseUrl) {
        mRepository.getSipSupportServiceAttach(baseUrl);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerPaymentID(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFilesViaCustomerPaymentID(baseUrl);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerProductID(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFilesViaCustomerProductID(baseUrl);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerSupportID(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFilesViaCustomerSupportID(baseUrl);
    }

    public void getSipSupportServiceGetAttachmentFileViaAttachIDRetrofitInstance(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentFileViaAttachIDRetrofitInstance(baseUrl);
    }

    public void attach(String userLoginKey, AttachInfo attachInfo) {
        mRepository.attach(userLoginKey, attachInfo);
    }

    public void getAttachmentFilesViaCustomerPaymentID(String userLoginKey, int customerPaymentID, boolean LoadFileData) {
        mRepository.getAttachmentFilesViaCustomerPaymentID(userLoginKey, customerPaymentID, LoadFileData);
    }

    public void fetchFileWithCustomerProductID(String userLoginKey, int customerProductID, boolean LoadFileData) {
        mRepository.fetchFileWithCustomerProductID(userLoginKey, customerProductID, LoadFileData);
    }

    public void fetchFileWithCustomerSupportID(String userLoginKey, int customerSupportID, boolean LoadFileData) {
        mRepository.fetchFileWithCustomerSupportID(userLoginKey, customerSupportID, LoadFileData);
    }

    public void fetchWithAttachID(String userLoginKey, int attachID, boolean loadFileData) {
        mRepository.fetchWithAttachID(userLoginKey, attachID, loadFileData);
    }

    public void deleteAttach(String userLoginKey, int attachID) {
        mRepository.deleteAttach(userLoginKey, attachID);
    }

    public void getSipSupportServiceDeleteAttach(String baseUrl) {
        mRepository.getSipSupportServiceDeleteAttach(baseUrl);
    }

    public void getSipSupportServiceGetAttachmentListByPaymentID(String baseUrl) {
        mRepository.getSipSupportServiceGetAttachmentListByPaymentID(baseUrl);
    }

    public void fetchAttachmentsByPaymentID(String userLoginKey, int paymentID, boolean LoadFileData) {
        mRepository.fetchAttachmentsByPaymentID(userLoginKey, paymentID, LoadFileData);
    }
}
