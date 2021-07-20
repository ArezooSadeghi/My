package com.example.sipsupporterapp.eventbus;

import com.example.sipsupporterapp.model.CaseTypeResult;

public class CaseTypesEvent {

    private CaseTypeResult caseTypeResult;

    public CaseTypesEvent(CaseTypeResult caseTypeResult) {
        this.caseTypeResult = caseTypeResult;
    }

    public CaseTypeResult getCaseTypeResult() {
        return caseTypeResult;
    }

    public void setCaseTypeResult(CaseTypeResult caseTypeResult) {
        this.caseTypeResult = caseTypeResult;
    }
}
