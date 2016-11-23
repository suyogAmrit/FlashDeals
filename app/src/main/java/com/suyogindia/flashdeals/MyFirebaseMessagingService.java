package com.suyogindia.flashdeals;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.suyogindia.helpers.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by suyogcomputech on 24/10/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().get("data"));

        //Calling method to generate notification
        // sendNotification(remoteMessage.getNotification().getBody());

        try {
            JSONObject notificationObject = new JSONObject(remoteMessage.getData().get("data"));
            sendNotification(notificationObject);
        } catch (JSONException e) {
            Log.e(AppConstants.ERROR, e.getLocalizedMessage());
        }

    }

    private void sendNotification(JSONObject notificationObject) throws JSONException {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);


        int notificationType = notificationObject.getInt(AppConstants.NOTIFICATIONTYPE);

        Intent intent = null;
        if (notificationType == 1) {
            builder.setContentTitle("Flash Deal Order Update");
            builder.setTicker("Flash Deal Order Update");

            intent = new Intent(this, OrdersActivity.class);

        } else if (notificationType == 0) {
            intent = new Intent(this, MainActivity.class);
            builder.setTicker("Deals Of the Day");
            builder.setContentTitle("Deals Of the Day");
        }
        String messageBody = notificationObject.getString(AppConstants.MESSAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(messageBody));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationType, builder.build());

    }
}