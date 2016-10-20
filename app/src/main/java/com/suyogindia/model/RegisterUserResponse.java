package com.suyogindia.model;

/**
 * Created by suyogcomputech on 13/10/16.
 */

public class RegisterUserResponse {
    private String status,email,message;

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RegisterUserResponse{" +
                "status='" + status + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
