package vmart.example.mypc.vedasmart.pushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import vmart.example.mypc.vedasmart.R;

public class MyFirebaseCloudMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "FCM_01";
    private static final String CHANNEL_NAME = "FCM_CHANNEL";
    public static final String CHANNEL_DESCRIPTION = "FIREBASE_CLOUD_MESSAGING_CHANNEL";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;
        if (remoteMessage.getNotification() != null) {
            Log.d("Push Notification Body", remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                Log.d("PushNotifydata", jsonObject.toString());
                showNotification(jsonObject.getString("title"), jsonObject.getString("body"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String token) {
        saveFcmTokenToLocal(token);
        sendRegistrationTokenToServer(token);
        super.onNewToken(token);
    }

    private void saveFcmTokenToLocal(String token) {
        FcmTokenPreference.getInstance(getApplicationContext()).saveFcmToken(token);
    }

    private void sendRegistrationTokenToServer(String token) {

    }

    private void showNotification(String title, String message) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_app) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true);// clear notification after click
        mNotificationManager.notify(0, mBuilder.build());
    }
}
