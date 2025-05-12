package com.mordekai.poggtech.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.NotificationFlagHelper;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Nova notificação";
        String body = "Tens uma nova mensagem";
        String type = "home";

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();

            if (data.containsKey("title")) title = data.get("title");
            if (data.containsKey("body")) body = data.get("body");
            if (data.containsKey("type")) type = data.get("type");
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigate_to", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        sendNotification(title, body, intent);
        MessageNotifier.notifyMessage();
        NotificationFlagHelper.setNewMessageFlag(getApplicationContext(), true);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("has_new_message", true).apply();
    }

    private void sendNotification(String title, String body, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT
        );

        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(som)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel("default") == null) {
                NotificationChannel canal = new NotificationChannel("default", "Notificações", NotificationManager.IMPORTANCE_DEFAULT);
                canal.setDescription("Canal usado para notificações gerais");
                manager.createNotificationChannel(canal);
            }
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
