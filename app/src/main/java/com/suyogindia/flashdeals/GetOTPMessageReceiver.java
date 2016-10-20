package com.suyogindia.flashdeals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.suyogindia.helpers.AppConstants;

/**
 * Created by suyogcomputech on 10/10/16.
 */

public class GetOTPMessageReceiver extends BroadcastReceiver {
    private static final String TAG = GetOTPMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            Object[] pdusObj = (Object[]) intent.getExtras().get("pdus");
            SmsMessage smsMessage = null;
            if (pdusObj != null) {
                for (Object obj : pdusObj) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) obj, "3ggp");
                    } else {
                        smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                    }
                }
            }
            if (smsMessage != null) {
                String senderAddress = smsMessage.getDisplayOriginatingAddress();
                String message = smsMessage.getDisplayMessageBody();
                if (!senderAddress.toLowerCase().contains(AppConstants.SMS_ORIGIN.toLowerCase())) {
                    Log.e(TAG, "SMS is not for our app!");
                    return;
                }

                // verification code from sms
                String verificationCode = getVerificationCode(message);

                Log.e(TAG, "OTP received: " + verificationCode);

                Intent hhtpIntent = new Intent(context, VerifyOTPService.class);
                hhtpIntent.putExtra(AppConstants.OTP, verificationCode);
                context.startService(hhtpIntent);
            }
        }
    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(AppConstants.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }

}
