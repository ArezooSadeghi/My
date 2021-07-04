package com.example.sipsupporterapp.model;

public class CommentResult {

    private String errorCode;
    private String error;
    private CommentInfo[] comments;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public CommentInfo[] getComments() {
        return comments;
    }

    public void setComments(CommentInfo[] comments) {
        this.comments = comments;
    }
}
