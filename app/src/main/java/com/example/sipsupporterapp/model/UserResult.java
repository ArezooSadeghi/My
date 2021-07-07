package com.example.sipsupporterapp.model;

public class UserResult {

    private String errorCode;
    private String error;
    private UserInfo[] users;

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

    public UserInfo[] getUsers() {
        return users;
    }

    public void setUsers(UserInfo[] users) {
        this.users = users;
    }

    public class UserInfo {

        private int userID;
        private boolean disable;
        private String userFullName;
        private String userLoginKey;

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public boolean isDisable() {
            return disable;
        }

        public void setDisable(boolean disable) {
            this.disable = disable;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public String getUserLoginKey() {
            return userLoginKey;
        }

        public void setUserLoginKey(String userLoginKey) {
            this.userLoginKey = userLoginKey;
        }
    }

    public class UserLoginParameter {

        private String userName;
        private String password;

        public UserLoginParameter(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
