package com.example.sipsupporterapp.eventbus;

import com.example.sipsupporterapp.model.BankAccountResult;

public class PostBankAccountResultEvent {

    private BankAccountResult bankAccountResult;

    public PostBankAccountResultEvent(BankAccountResult bankAccountResult) {
        this.bankAccountResult = bankAccountResult;
    }

    public BankAccountResult getBankAccountResult() {
        return bankAccountResult;
    }

    public void setBankAccountResult(BankAccountResult bankAccountResult) {
        this.bankAccountResult = bankAccountResult;
    }
}
