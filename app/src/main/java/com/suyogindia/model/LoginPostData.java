package com.suyogindia.model;

/**
 * Created by suyogcomputech on 14/10/16.
 */

public class LoginPostData {
    private String userId,email,password;

    public LoginPostData(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
}
