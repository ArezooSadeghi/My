package com.example.sipsupporterapp.eventbus;

import com.example.sipsupporterapp.model.CaseProductResult;

public class NavigateEvent {
    private CaseProductResult.CaseProductInfo caseProductInfo;

    public NavigateEvent(CaseProductResult.CaseProductInfo caseProductInfo) {
        this.caseProductInfo = caseProductInfo;
    }

    public CaseProductResult.CaseProductInfo getCaseProductInfo() {
        return caseProductInfo;
    }

    public void setCaseProductInfo(CaseProductResult.CaseProductInfo caseProductInfo) {
        this.caseProductInfo = caseProductInfo;
    }
}
