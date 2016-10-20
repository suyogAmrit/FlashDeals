package com.suyogindia.model;

/**
 * Created by suyogcomputech on 13/10/16.
 */

public class VerifyOtpPostData {
    String otp,phoneNumber;

    public VerifyOtpPostData(String otp, String phoneNumber) {
        this.otp = otp;
        this.phoneNumber = phoneNumber;
    }
}
