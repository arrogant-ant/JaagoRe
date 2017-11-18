package iris.jaagore.sabita_sant.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Sud on 10/6/17.
 */

public class AlarmNotification {
    String alarmText;
    Alarm alarm;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Context context;

    public AlarmNotification(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        alarm = new Alarm(context);
    }

    public void setPending(long timeInMillis) {

        cancel();
        alarmText = alarm.getAlarmTime();
        PendingIntent alarmIntent = PendingIntent.getActivity(context, 0, new Intent(context, AddAlarm.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setContentTitle("Alarm")
                .setSmallIcon(R.drawable.icon)
                .setContentText("Next Alarm Pending at " + alarmText)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setContentIntent(alarmIntent);

        notificationManager
                .notify(2, builder.build());
    }

    public void setActive() {
        alarmText = alarm.getAlarmTime();
        final String ALERT = "JAAGO RE";
        final String TITLE = "It's " + alarmText;
        final String MSG = "TIME TO WAKE UP";
        Intent screen = new Intent(context, AlarmScreen.class);
        PendingIntent pending_back = PendingIntent.getActivity(context, 0, screen, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setTicker(ALERT)
                .setContentTitle(TITLE)
                .setContentText(MSG)
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(R.drawable.snooze, "Stop", pending_back);//change pending_back to snooze alarm
        builder.setContentIntent(pending_back);
        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setContentIntent(pending_back);
        builder.setAutoCancel(true);
        builder.setDefaults(1);
        notificationManager.notify(1, builder.build());
    }

    public void cancel() {

        notificationManager.cancelAll();
    }

}
