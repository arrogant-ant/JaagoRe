package iris.example.sabita_sant.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;

import iris.example.sabita_sant.alarm.backend.Alarm;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Sud on 10/6/17.
 */

public class AlarmNotification {
    String alarmText;
    //Alarm alarm;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Context context;
    private SimpleDateFormat tf;

    public AlarmNotification(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
         tf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        // alarm = new Alarm(context);
    }

    public void setPending(long timeInMillis) {

        cancel();
        alarmText = tf.format(timeInMillis);

        PendingIntent alarmIntent = PendingIntent.getActivity(context, 0, new Intent(context, Home.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setContentTitle("Alarm")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText("Next Alarm Pending at " + alarmText)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setContentIntent(alarmIntent);

        notificationManager
                .notify(2, builder.build());
    }

    public void setActive(Alarm alarm) {
        //SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
        alarmText = tf.format(alarm.getAlarmTime());
        final String ALERT = "JAAGO RE";
        final String TITLE = alarm.getLabel();
        final String MSG = "It's " + alarmText;
        Intent screen = new Intent(context, AlarmScreen.class);
        PendingIntent pending_back = PendingIntent.getActivity(context, 0, screen, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setTicker(ALERT)
                .setContentTitle(TITLE)
                .setContentText(MSG)
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(R.drawable.ic_snooze, "Stop", pending_back);//change pending_back to snooze alarm
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
