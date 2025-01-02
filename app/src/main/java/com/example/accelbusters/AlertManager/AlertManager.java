package com.example.accelbusters.AlertManager;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.accelbusters.FullScreenWarningActivity;
import com.example.accelbusters.R;
import com.google.android.gms.nearby.Nearby;

import java.nio.charset.StandardCharsets;


public class AlertManager {
    private static final String TAG = "AlertManager";
    private final Context context;
    private Handler handler = new Handler();
    private boolean isLevel2AlertRepeated = false; // 用于记录Level 2是否已重复通知
    public AlertManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "alert_channel",
                    "Alert Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for alert notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }
//    public void showInAppAlert(String message) {
//        new AlertDialog.Builder(context)
//                .setTitle("危险警报")
//                .setMessage(message)
//                .setPositiveButton("确认", null)
//                .setCancelable(false)
//                .show();
//    }
//
//    public void sendSystemNotification(String message, int level) {
//        String channelId = "danger_alert";
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationChannel channel = new NotificationChannel(
//                channelId,
//                "危险通知",
//                NotificationManager.IMPORTANCE_HIGH
//        );
//        channel.setDescription("危险警报通知");
//        channel.enableLights(true);
//        channel.enableVibration(true);
//        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
//        notificationManager.createNotificationChannel(channel);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("危险警报")
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true);
//
//        notificationManager.notify(1, builder.build());
//    }

    // Level 2 警报逻辑
    public void triggerLevel2Alert() {
        showNotification("注意が必要", "level 2");
        playNotificationSound();
        if (!isLevel2AlertRepeated) {
            handler = new Handler(Looper.getMainLooper());
            // 重新触发通知
            handler.postDelayed(this::triggerLevel2Alert, 10000); // 每10秒重新触发通知
        }
    }

    // Level 3 警报逻辑
    public void triggerLevel3Alert() {
        showNotification("行動が必要", "level 3");
        playWarningSound(3000); // 每隔3秒播放一次警报音
    }

    // Level 4 警报逻辑
    public void triggerLevel4Alert() {
        showFullScreenWarning("即時行動が必要", "level 4");
        playContinuousAlarm();
    }

    // 停止 Level 2 警报
    public void stopLevel2Alert() {
        isLevel2AlertRepeated = true; // 停止警报
        handler.removeCallbacksAndMessages(null); // 清除所有挂起的回调任务
    }

    // 显示通知
    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alert_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 记得替换图标
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    // 显示全屏警告（Level 4 专用）
    private void showFullScreenWarning(String title, String content) {
        // 您可以使用 Activity 实现全屏警告界面，并从这里启动该 Activity
        Intent intent = new Intent(context, FullScreenWarningActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 播放通知音
    private void playNotificationSound() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 1000); // 持续1秒
    }

    // 播放警报音（间隔播放）
    private void playWarningSound(int interval) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000); // 持续1秒
                handler.postDelayed(this, interval); // 间隔播放
            }
        }, interval);
    }

    // 播放持续的警报音
    private void playContinuousAlarm() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L); // 持续播放
    }


}
