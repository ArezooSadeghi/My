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
    private SingleLiveEvent<String> error;

    public PaymentSubjectViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
        paymentSubjectsResultSingleLiveEvent = repository.getPaymentSubjectsResultSingleLiveEvent();
        error = repository.getError();
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectsResultSingleLiveEvent() {
        return paymentSubjectsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getError() {
        return error;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServicePaymentSubjectResult(String userLoginKey) {
        repository.getSipSupporterServicePaymentSubjectResult(userLoginKey);
    }

    public void fetchPaymentSubjects(String path, String baseUrl) {
        repository.fetchPaymentSubjects(path, baseUrl);
    }
}
