package com.example.admin.footballer.Models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String userName;
    private String photoUrl;
    private int userType;
    private String birthday;
    private String phoneNumber;

    public User() {

    }

    public User(String userName, String photoUrl, int type, String birthday, String phoneNumber) {
        this.userName = userName;
        this.userType = type;
        this.photoUrl = photoUrl;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }


    public String getUserName() {
        return userName;
    }

    public int getUserType() { return userType;}

    public String getBirthday() { return birthday;}

    public String getPhoneNumber() { return phoneNumber;}

    public String getPhotoUrl() { return photoUrl;}
}
