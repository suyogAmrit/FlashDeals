package com.suyogindia.flashdeals;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import com.suyogindia.helpers.AppConstants;

/**
 * Created by suyogcomputech on 10/10/16.
 */

public class VerifyOTPService extends IntentService {
    public VerifyOTPService() {
        super("VerifyOTPService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getExtras().getString(AppConstants.OTP);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(VerifyOTPActivity.OTPReceiver.ACTION);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(AppConstants.OTP, otp);
//            broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
            sendBroadcast(broadcastIntent);
            stopSelf();
        }
    }
}
