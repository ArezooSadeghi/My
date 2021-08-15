package com.example.sipsupporterapp.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.sipsupporterapp.utils.Converter;

public class AssignResult {
    private String errorCode;
    private String error;
    private AssignInfo[] assigns;

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

    public AssignInfo[] getAssigns() {
        return assigns;
    }

    public void setAssigns(AssignInfo[] assigns) {
        this.assigns = assigns;
    }

    public static class AssignInfo {
        private int assignID;
        private int caseID;
        private int caseProductID;
        private int assignUserID;
        private int parentID;
        private int userID;
        private long addTime;
        private long finishTime;
        private long seenTime;
        private boolean SeenAssigner;
        private boolean seen;
        private boolean finish;
        private String assignUserFullName;
        private String description;
        private String userFullName;

        public int getAssignID() {
            return assignID;
        }

        public void setAssignID(int assignID) {
            this.assignID = assignID;
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

        public int getAssignUserID() {
            return assignUserID;
        }

        public void setAssignUserID(int assignUserID) {
            this.assignUserID = assignUserID;
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

        public long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }

        public long getSeenTime() {
            return seenTime;
        }

        public void setSeenTime(long seenTime) {
            this.seenTime = seenTime;
        }

        public boolean isSeenAssigner() {
            return SeenAssigner;
        }

        public void setSeenAssigner(boolean seenAssigner) {
            SeenAssigner = seenAssigner;
        }

        public boolean isSeen() {
            return seen;
        }

        public void setSeen(boolean seen) {
            this.seen = seen;
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public String getAssignUserFullName() {
            return assignUserFullName;
        }

        public void setAssignUserFullName(String assignUserFullName) {
            this.assignUserFullName = assignUserFullName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        @BindingAdapter({"convertLetter"})
        public static void converter(TextView textView, String text) {
            textView.setText(Converter.letterConverter(text));
        }
    }
}
