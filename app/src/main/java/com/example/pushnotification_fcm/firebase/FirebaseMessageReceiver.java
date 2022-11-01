package com.example.pushnotification_fcm.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.pushnotification_fcm.MainActivity;
import com.example.pushnotification_fcm.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/*
 * FirebaseMessagingService - This service is required to do any type of message handling beyond just receiving notifications,
 *                                           while the client app runs in the background.
 */
public class FirebaseMessageReceiver extends FirebaseMessagingService {

    /*
    *  onMessageReceived --   handle 2 events:
    * 1. If Notification contains any data payload from server
    * 2. ...............contains any notification payload  (sent via the Firebase Admin SDK)
    */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {       // RemoteMessage: is the object of the message passed using FCM

        if (remoteMessage.getNotification() != null) {
            /*
            *     Here, 'title' and 'message' are the assumed names of JSON attributes ( here we don't have any data payload-- in this case we exract title & message from LIST)
            * */
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);
        }

    }

    /* Method to show notitfication */
    private void showNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        // to clear the activity stack till now.

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id).setSmallIcon(R.drawable.ic_smile).setAutoCancel(true).setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setOnlyAlertOnce(true).setContentIntent(pendingIntent);

        /*
        * A customized design for the notification can be set only for Android versions 4.1 and above
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getCustomDesign(title, message));
        } else {
            // customized layout cannot be used , thus layout is set here
            builder = builder.setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_smile);
        }
/*
* Obj, to Notify the user of event  that happen in the background.
* */
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }

    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon, R.drawable.ic_smile);

        return remoteViews;
    }

}
