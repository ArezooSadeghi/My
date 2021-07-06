package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CommentInfo;
import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CommentViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentInfo> editClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult> commentsByCaseIDResultSingleLiveEvent;

    private SingleLiveEvent<CommentResult> addCommentResultSingleLiveEvent;

    private SingleLiveEvent<CommentResult> deleteCommentResultSingleLiveEvent;

    private SingleLiveEvent<CommentResult> editCommentResultSingleLiveEvent;

    private SingleLiveEvent<Boolean> refreshComments = new SingleLiveEvent<>();

    public CommentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        commentsByCaseIDResultSingleLiveEvent = repository.getCommentsByCaseIDResultSingleLiveEvent();

        addCommentResultSingleLiveEvent = repository.getAddCommentResultSingleLiveEvent();

        deleteCommentResultSingleLiveEvent = repository.getDeleteCommentResultSingleLiveEvent();

        editCommentResultSingleLiveEvent = repository.getEditCommentResultSingleLiveEvent();
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<CommentInfo> getEditClicked() {
        return editClicked;
    }

    public SingleLiveEvent<CommentResult> getCommentsByCaseIDResultSingleLiveEvent() {
        return commentsByCaseIDResultSingleLiveEvent;
    }

    public SingleLiveEvent<CommentResult> getAddCommentResultSingleLiveEvent() {
        return addCommentResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getRefreshComments() {
        return refreshComments;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public SingleLiveEvent<CommentResult> getDeleteCommentResultSingleLiveEvent() {
        return deleteCommentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CommentResult> getEditCommentResultSingleLiveEvent() {
        return editCommentResultSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCommentsByCaseID(String baseUrl) {
        repository.getSipSupporterServiceCommentsByCaseID(baseUrl);
    }

    public void getSipSupporterServiceAddComment(String baseUrl) {
        repository.getSipSupporterServiceAddComment(baseUrl);
    }

    public void getSipSupporterServiceDeleteComment(String baseUrl) {
        repository.getSipSupporterServiceDeleteComment(baseUrl);
    }

    public void getSipSupporterServiceEditComment(String baseUrl) {
        repository.getSipSupporterServiceEditComment(baseUrl);
    }

    public void fetchCommentsByCaseID(String path, String userLoginKey, int caseID) {
        repository.fetchCommentsByCaseID(path, userLoginKey, caseID);
    }

    public void addComment(String path, String userLoginKey, CommentInfo commentInfo) {
        repository.addComment(path, userLoginKey, commentInfo);
    }

    public void deleteComment(String path, String userLoginKey, int commentID) {
        repository.deleteComment(path, userLoginKey, commentID);
    }

    public void editComment(String path, String userLoginKey, CommentInfo commentInfo) {
        repository.editComment(path, userLoginKey, commentInfo);
    }
}
