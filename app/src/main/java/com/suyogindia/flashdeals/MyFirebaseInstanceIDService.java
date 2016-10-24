package com.suyogindia.flashdeals;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.suyogindia.helpers.AppConstants;
import com.suyogindia.helpers.AppHelpers;

/**
 * Created by suyogcomputech on 24/10/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    public static void sendRegistrationToServer(String refreshedToken, String android_id, String token) {
        //You can implement this method to store the token on your server
        //Not required for current project


    }

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences shrUserProfile = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        final String userID = shrUserProfile.getString(AppConstants.USERID, "na");
        if (AppHelpers.isConnectingToInternet(MyFirebaseInstanceIDService.this))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendRegistrationToServer(refreshedToken, android_id, userID);

                }
            }).start();

    }
}