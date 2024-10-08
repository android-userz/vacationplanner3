

package com.example.vacationplanner.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.vacationplanner.database.Excursion;
import com.example.vacationplanner.database.Vacation;

import java.util.Calendar;
import java.util.Date;

public class NotificationHelper {

    private Context context;

    private static final String VACATION_START_NOTIFICATION_TYPE = "vacation_start";
    private static final String VACATION_END_NOTIFICATION_TYPE = "vacation_end";
    private static final String EXCURSION_NOTIFICATION_TYPE = "excursion";

    public NotificationHelper(Context context) {
        this.context = context;
    }

    /**
     * Schedule a notification for vacation start date.
     *
     * @param vacation The Vacation object containing details.
     */
    public void scheduleVacationStartNotification(Vacation vacation) {
        scheduleNotification(
                vacation.getStartDate(),
                vacation.getId(),
                VACATION_START_NOTIFICATION_TYPE,
                vacation.getTitle(),
                "Vacation \"" + vacation.getTitle() + "\" is starting today!"
        );
    }

    /**
     * Schedule a notification for vacation end date.
     *
     * @param vacation The Vacation object containing details.
     */
    public void scheduleVacationEndNotification(Vacation vacation) {
        // Use a unique request code by adding a constant offset to the vacation ID
        int requestCode = vacation.getId() + 10000;
        scheduleNotification(
                vacation.getEndDate(),
                requestCode,
                VACATION_END_NOTIFICATION_TYPE,
                vacation.getTitle(),
                "Vacation \"" + vacation.getTitle() + "\" has ended today!"
        );
    }

    /**
     * Schedule a notification for an excursion.
     *
     * @param excursion The Excursion object containing details.
     */
    public void scheduleExcursionNotification(Excursion excursion) {
        scheduleNotification(
                excursion.getDate(),
                excursion.getId(),
                EXCURSION_NOTIFICATION_TYPE,
                excursion.getTitle(),
                "Excursion \"" + excursion.getTitle() + "\" is starting today!"
        );
    }

    /**
     * General method to schedule notifications.
     *
     * @param date            The date of the event.
     * @param requestCode     Unique request code for PendingIntent.
     * @param notificationType Type of notification (vacation_start, vacation_end, excursion).
     * @param title           Title of the event.
     * @param message         Message to display in the notification.
     */
    private void scheduleNotification(Date date, int requestCode, String notificationType, String title, String message) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notification_type", notificationType);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Schedule notification at 9 AM on the event day
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Ensure the scheduled time is in the future
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    /**
     * Cancel a scheduled notification.
     *
     * @param requestCode Unique request code used to schedule the notification.
     */
    /**
     * Cancel a scheduled notification.
     *
     * @param requestCode Unique request code used to schedule the notification.
     */
    public void cancelNotification(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }

}
