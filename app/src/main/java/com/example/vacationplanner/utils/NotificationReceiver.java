

package com.example.vacationplanner.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.vacationplanner.MainActivity;
import com.example.vacationplanner.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vacation_planner_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationType = intent.getStringExtra("notification_type");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        if (notificationType == null || title == null || message == null) {
            return;
        }

        // Create Notification Channel if necessary
        createNotificationChannel(context);

        // Intent to open the app when notification is tapped
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Choose appropriate icon based on notification type
        int iconResource = R.drawable.ic_vacation; // Default icon
        switch (notificationType) {
            case "vacation_start":
                iconResource = R.drawable.ic_vacation_start;
                break;
            case "vacation_end":
                iconResource = R.drawable.ic_vacation_end;
                break;
            case "excursion":
                iconResource = R.drawable.ic_excursion;
                break;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconResource)
                .setContentTitle("Vacation Planner Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Use unique notification IDs to allow multiple notifications
        int notificationId = (int) System.currentTimeMillis();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    /**
     * Create a Notification Channel for Android O and above.
     *
     * @param context The application context.
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Vacation Planner Channel";
            String description = "Channel for Vacation Planner reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
