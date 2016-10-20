package com.suyogindia.model;

/**
 * Created by suyogcomputech on 13/10/16.
 */

public class RegisterUserPostData {
    private String userId,name,email,password;

    public RegisterUserPostData(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegisterUserPostData{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
