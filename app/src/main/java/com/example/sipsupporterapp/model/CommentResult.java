package com.example.sipsupporterapp.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.sipsupporterapp.utils.Converter;

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

    public static class CommentInfo {
        private int commentID;
        private int caseID;
        private int caseProductID;
        private int assignID;
        private int parentID;
        private int userID;
        private long addTime;
        private String userFullName;
        private String comment;

        public int getCommentID() {
            return commentID;
        }

        public void setCommentID(int commentID) {
            this.commentID = commentID;
        }

        public int getCaseID() {
            return caseID;
        }

        public void setCaseID(int caseID) {
            this.caseID = caseID;
        }

        public int getCaseProductID() {
            return caseProductID;
        }

        public void setCaseProductID(int caseProductID) {
            this.caseProductID = caseProductID;
        }

        public int getAssignID() {
            return assignID;
        }

        public void setAssignID(int assignID) {
            this.assignID = assignID;
        }

        public int getParentID() {
            return parentID;
        }

        public void setParentID(int parentID) {
            this.parentID = parentID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @BindingAdapter({"convertLetter"})
        public static void converter(TextView textView, String text) {
            textView.setText(Converter.letterConverter(text));
        }
    }
}
