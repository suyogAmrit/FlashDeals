package com.suyogindia.model;

/**
 * Created by suyogcomputech on 24/10/16.
 */

public class PostTokenData {
    private String token, deviceId, userId;

    public PostTokenData(String token, String deviceId, String userid) {
        this.token = token;
        this.deviceId = deviceId;
        this.userId = userid;
    }
}
