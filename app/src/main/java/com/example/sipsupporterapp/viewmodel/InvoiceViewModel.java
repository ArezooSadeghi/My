package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class InvoiceViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    public InvoiceViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());
    }
}
