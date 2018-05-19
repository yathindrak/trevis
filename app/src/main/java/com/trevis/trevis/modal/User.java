package com.trevis.trevis.modal;

public class User {
    private String userId;
    private String deviceToken;
    private String name;
    private String email;
    private String password;
    private String mobile;
    private String gender;


    public User(){}

    public User(String userId, String deviceToken, String name, String email, String password, String mobile, String gender) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
