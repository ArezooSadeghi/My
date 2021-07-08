package com.example.sipsupporterapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.repository.SipSupporterRepository;

public class CommentViewModel extends AndroidViewModel {
    private SipSupporterRepository repository;

    private SingleLiveEvent<Integer> deleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult.CommentInfo> editClicked = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult> commentsByCaseIDResultSingleLiveEvent;

    private SingleLiveEvent<CommentResult> addCommentResultSingleLiveEvent;

    private SingleLiveEvent<CommentResult> deleteCommentResultSingleLiveEvent;

    private SingleLiveEvent<CommentResult> editCommentResultSingleLiveEvent;

    private SingleLiveEvent<Boolean> refreshComments = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    public CommentViewModel(@NonNull Application application) {
        super(application);

        repository = SipSupporterRepository.getInstance(getApplication());

        commentsByCaseIDResultSingleLiveEvent = repository.getCommentsByCaseIDResultSingleLiveEvent();

        addCommentResultSingleLiveEvent = repository.getAddCommentResultSingleLiveEvent();

        deleteCommentResultSingleLiveEvent = repository.getDeleteCommentResultSingleLiveEvent();

        editCommentResultSingleLiveEvent = repository.getEditCommentResultSingleLiveEvent();

        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<Integer> getDeleteClicked() {
        return deleteClicked;
    }

    public SingleLiveEvent<CommentResult.CommentInfo> getEditClicked() {
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

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSipSupporterServiceCommentResult(String baseUrl) {
        repository.getSipSupporterServiceCommentResult(baseUrl);
    }

    public void getSipSupporterServiceAddComment(String baseUrl) {
        repository.getSipSupporterServiceCommentResult(baseUrl);
    }

    public void fetchCommentsByCaseID(String path, String userLoginKey, int caseID) {
        repository.fetchCommentsByCaseID(path, userLoginKey, caseID);
    }

    public void addComment(String path, String userLoginKey, CommentResult.CommentInfo commentInfo) {
        repository.addComment(path, userLoginKey, commentInfo);
    }

    public void deleteComment(String path, String userLoginKey, int commentID) {
        repository.deleteComment(path, userLoginKey, commentID);
    }

    public void editComment(String path, String userLoginKey, CommentResult.CommentInfo commentInfo) {
        repository.editComment(path, userLoginKey, commentInfo);
    }
}
