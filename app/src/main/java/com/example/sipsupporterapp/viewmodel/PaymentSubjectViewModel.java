package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class PaymentSubjectViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentSubjectsResultSingleLiveEvent;

    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<String> timeoutExceptionHappen;

    public PaymentSubjectViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        paymentSubjectsResultSingleLiveEvent = repository.getPaymentSubjectsResultSingleLiveEvent();
        errorPaymentSubjectsResultSingleLiveEvent = repository.getErrorPaymentSubjectsResultSingleLiveEvent();

        noConnection = repository.getNoConnection();
        timeoutExceptionHappen = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectsResultSingleLiveEvent() {
        return paymentSubjectsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentSubjectsResultSingleLiveEvent() {
        return errorPaymentSubjectsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappen() {
        return timeoutExceptionHappen;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServicePaymentSubjects(String userLoginKey) {
        repository.getSipSupporterServicePaymentSubjects(userLoginKey);
    }

    public void fetchPaymentSubjects(String path, String baseUrl) {
        repository.fetchPaymentSubjects(path, baseUrl);
    }
}
