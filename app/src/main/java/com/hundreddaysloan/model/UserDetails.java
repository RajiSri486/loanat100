package com.hundreddaysloan.model;

public class UserDetails {

    private String userName;
    private String mobileNo;
    private boolean userStatus;

    public UserDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserDetails(String userName, String mobileNo, boolean userStatus) {
        this.userName = userName;
        this.mobileNo = mobileNo;
        this.userStatus = userStatus;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    public boolean getUserStatus() {
        return userStatus;
    }
    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }
}
