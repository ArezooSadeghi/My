package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class AssignViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<AssignResult.AssignInfo> editClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> registerCommentClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<UserResult> usersResultSingleLiveEvent;
    private SingleLiveEvent<AssignResult> addAssignResultSingleLiveEvent;
    private SingleLiveEvent<AssignResult> assignsResultSingleLiveEvent;
    private SingleLiveEvent<Boolean> refreshAssigns = new SingleLiveEvent<>();
    private SingleLiveEvent<AssignResult> editAssignResultSingleLiveEvent;
    private SingleLiveEvent<AssignResult> deleteAssignResultSingleLiveEvent;
    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    public AssignViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        usersResultSingleLiveEvent = repository.getUsersResultSingleLiveEvent();

        addAssignResultSingleLiveEvent = repository.getAddAssignResultSingleLiveEvent();

        assignsResultSingleLiveEvent = repository.getAssignsResultSingleLiveEvent();

        editAssignResultSingleLiveEvent = repository.getEditAssignResultSingleLiveEvent();

        deleteAssignResultSingleLiveEvent = repository.getDeleteAssignResultSingleLiveEvent();
    }

    public SingleLiveEvent<AssignResult.AssignInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<Boolean> getRegisterCommentClicked() {
        return registerCommentClicked;
    }

    public SingleLiveEvent<UserResult> getUsersResultSingleLiveEvent() {
        return usersResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getAddAssignResultSingleLiveEvent() {
        return addAssignResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getAssignsResultSingleLiveEvent() {
        return assignsResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRefreshAssigns() {
        return refreshAssigns;
    }

    public SingleLiveEvent<AssignResult> getEditAssignResultSingleLiveEvent() {
        return editAssignResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getDeleteAssignResultSingleLiveEvent() {
        return deleteAssignResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceUsers(String baseUrl) {
        repository.getSipSupporterServiceUsers(baseUrl);
    }

    public void getSipSupporterServiceAddAssign(String baseUrl) {
        repository.getSipSupporterServiceAddAssign(baseUrl);
    }

    public void getSipSupporterServiceAssigns(String baseUrl) {
        repository.getSipSupporterServiceAssigns(baseUrl);
    }

    public void getSipSupporterServiceEditAssign(String baseUrl) {
        repository.getSipSupporterServiceEditAssign(baseUrl);
    }

    public void getSipSupporterServiceDeleteAssign(String baseUrl) {
        repository.getSipSupporterServiceDeleteAssign(baseUrl);
    }

    public void fetchUsers(String path, String userLoginKey) {
        repository.fetchUsers(path, userLoginKey);
    }

    public void addAssign(String path, String userLoginKey, AssignResult.AssignInfo assignInfo) {
        repository.addAssign(path, userLoginKey, assignInfo);
    }

    public void fetchAssigns(String path, String userLoginKey, int caseID) {
        repository.fetchAssigns(path, userLoginKey, caseID);
    }

    public void editAssign(String path, String userLoginKey, AssignResult.AssignInfo assignInfo) {
        repository.editAssign(path, userLoginKey, assignInfo);
    }

    public void deleteAssign(String path, String userLoginKey, int assignID) {
        repository.deleteAssign(path, userLoginKey, assignID);
    }
}
