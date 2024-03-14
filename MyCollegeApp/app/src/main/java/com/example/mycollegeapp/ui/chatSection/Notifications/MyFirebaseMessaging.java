package com.example.mycollegeapp.ui.chatSection.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mycollegeapp.MainActivity;
import com.example.mycollegeapp.ui.chatSection.MessageActivity;
import com.example.mycollegeapp.ui.profile.ProfileSharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private ProfileSharedPreferences spProfile;
    private HashMap<String,String> profileData;
    private static final String CHANNEL_ID = "com.example.mycollegeapp";
    private static final String CHANNEL_NAME = "chat";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        if (s != null) {
            updateToken(s);
        }
    }

    private void updateToken(String refreshToken) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat section").child("tokens");
            Token token = new Token(refreshToken);
            if(spProfile==null)
             spProfile=  new ProfileSharedPreferences(getApplicationContext());
            if(spProfile.checkLogin()){
                profileData=spProfile.getUserProfile();
                reference.child(profileData.get(ProfileSharedPreferences.KEY_REGNO)).setValue(token);
            }
    }

    private void sendNotification(RemoteMessage remoteMessage){

        String user = remoteMessage.getData().get("user");
        String role = remoteMessage.getData().get("role");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.putExtra("userid",user);
        intent.putExtra("userRole",role);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build());
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(new long[] { 0, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

            NotificationCompat.Builder notificationBuilder;
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
            notificationBuilder.setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .setSmallIcon(getResourceIdForResourceName(getApplicationContext(), icon))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setVibrate(new long[] { 0, 1000})
                    .setSound(RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION));

            notificationManager.notify(1,notificationBuilder.build());
    }

    private int getResourceIdForResourceName(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
        }
        return resourceId;
    }
}
