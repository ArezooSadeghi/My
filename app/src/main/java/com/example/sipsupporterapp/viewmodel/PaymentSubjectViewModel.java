package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupportRepository;

public class PaymentSubjectViewModel extends AndroidViewModel {
    private SipSupportRepository repository;

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectsResultSingleLiveEvent;
    private SingleLiveEvent<String> errorPaymentSubjectsResultSingleLiveEvent;

    private SingleLiveEvent<String> noConnection;
    private SingleLiveEvent<Boolean> timeoutExceptionHappen;

    public PaymentSubjectViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupportRepository.getInstance(getApplication());

        paymentSubjectsResultSingleLiveEvent = repository.getPaymentSubjectsResultSingleLiveEvent();
        errorPaymentSubjectsResultSingleLiveEvent = repository.getErrorPaymentSubjectsResultSingleLiveEvent();

        noConnection = repository.getNoConnection();
        timeoutExceptionHappen = repository.getTimeoutExceptionHappen();
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

    public SingleLiveEvent<Boolean> getTimeoutExceptionHappen() {
        return timeoutExceptionHappen;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServicePaymentSubjects(String userLoginKey) {
        repository.getSipSupporterServicePaymentSubjects(userLoginKey);
    }

    public void fetchPaymentSubjects(String baseUrl) {
        repository.fetchPaymentSubjects(baseUrl);
    }
}
