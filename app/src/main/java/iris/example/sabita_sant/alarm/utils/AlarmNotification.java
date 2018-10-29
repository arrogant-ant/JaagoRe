package iris.example.sabita_sant.alarm.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.backend.Alarm;
import iris.example.sabita_sant.alarm.backend.Quote;
import iris.example.sabita_sant.alarm.controller.QuoteHelper;
import iris.example.sabita_sant.alarm.views.AlarmScreen;
import iris.example.sabita_sant.alarm.views.Home;
import iris.example.sabita_sant.alarm.views.QuoteScreen;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Sud on 10/6/17.
 */

public class AlarmNotification {
    private final int ACTIVE_ALARM = 1;
    private final int PENDING_ALARM = 2;
    private final int QUOTES = 2;
    private final int SUGGEST_ALARM = 2;
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
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentText("Next Alarm Pending at " + alarmText)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setContentIntent(alarmIntent);

        notificationManager
                .notify(PENDING_ALARM, builder.build());
    }

    public void setActive(Alarm alarm) {
        alarmText = tf.format(alarm.getAlarmTime());
        final String ALERT = "Alarm Clock";
        final String TITLE = alarm.getLabel();
        final String MSG = "It's " + alarmText;
        Intent screen = new Intent(context, AlarmScreen.class);
        screen.putExtra(Constants.ALARM_ID_KEY, alarm.getId());
        PendingIntent pending_back = PendingIntent.getActivity(context, 0, screen, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setTicker(ALERT)
                .setContentTitle(TITLE)
                .setContentText(MSG)
                .setSmallIcon(R.drawable.ic_alarm);
        builder.setContentIntent(pending_back);
        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setContentIntent(pending_back);
        builder.setAutoCancel(true);
        builder.setDefaults(1);
        notificationManager.notify(ACTIVE_ALARM, builder.build());
    }

    public void cancel() {
        notificationManager.cancelAll();
    }

    public void showQuotesNotification() {
        final String ALERT = "Alarm Clock";
        final String TITLE = "Quote Of The Day";
        Quote quote = new QuoteHelper(context).getQuote();
        final String MSG = quote.getQuote();
        Intent screen = new Intent(context, QuoteScreen.class);
        PendingIntent pending_back = PendingIntent.getActivity(context, 0, screen, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setTicker(ALERT)
                .setContentTitle(TITLE)
                .setContentText(MSG)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentIntent(pending_back)//change pending_back to snooze alarm
                .setAutoCancel(true);
        notificationManager.notify(QUOTES, builder.build());
        // hack to show same quote on quote screen
        SharedPreferences preferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //   editor.putBoolean("isQuoteUsed", true);
        editor.putInt("pos", quote.getS_no());
        editor.apply();
    }

    public void suggestAlarmNotification() {
        final String ALERT = "Alarm Clock";
        final String TITLE = "Plan your day";
        final String MSG = "Every morning starts a new page in your story.";
        Intent screen = new Intent(context, Home.class);
        PendingIntent pending_back = PendingIntent.getActivity(context, 0, screen, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setTicker(ALERT)
                .setContentTitle(TITLE)
                .setContentText(MSG)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentIntent(pending_back)//change pending_back to snooze alarm
                .setAutoCancel(true);
        notificationManager.notify(SUGGEST_ALARM, builder.build());
    }
}
